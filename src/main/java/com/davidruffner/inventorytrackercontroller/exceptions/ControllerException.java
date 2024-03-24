package com.davidruffner.inventorytrackercontroller.exceptions;

import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode;
import com.davidruffner.inventorytrackercontroller.util.Logging.ErrorLogBuilder;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.getStrFromResponseStatus;
import static com.davidruffner.inventorytrackercontroller.util.Constants.CLIENT_IP_ATTR;
import static com.davidruffner.inventorytrackercontroller.util.Logging.ErrorLogBuilder.newErrorLogBuilder;

public class ControllerException extends Exception {
    private final ResponseStatusCode responseStatus;
    private final Class<?> callingClass;
    private final Optional<String> responseMessage;
    private final Optional<String> errorMessage;
    private final Optional<String> userId;
    private final Optional<String> deviceId;
    private final Optional<String> ipAddress;

    private ControllerException(Builder builder) {
        this.responseStatus = builder.responseStatus;
        this.callingClass = builder.callingClass;
        this.responseMessage = builder.responseMessage;
        this.errorMessage = (builder.errorMessage.isPresent()) ?
                builder.errorMessage : builder.responseMessage;
        this.userId = builder.userId;
        this.deviceId = builder.deviceId;
        this.ipAddress = builder.ipAddress;

        this.errorMessage.ifPresent(errMsg -> {
            ErrorLogBuilder errLog = newErrorLogBuilder(errMsg, this.callingClass);
            this.userId.ifPresent(errLog::withUserId);
            this.deviceId.ifPresent(errLog::withDeviceId);
            this.ipAddress.ifPresent(errLog::withIpAddress);
            errLog.log();
        });
    }

    /**
     * Predefined status codes to return to the client
     */
    public ResponseStatusCode getResponseStatus() {
        return responseStatus;
    }

    /**
     * The class that called the original exception
     */
    public Class<?> getCallingClass() {
        return callingClass;
    }

    /**
     * responseMessage is the message that gets sent back to
     * the client. This is used for generalized error messages
     * where you don't want to give the client more info than needed.
     */
    public Optional<String> getResponseMessage() {
        return responseMessage;
    }

    /**
     * errorMessage is the message that gets logged on the backend.
     * This can contain sensitive error information since it is not
     * being sent back to the client. If no errorMessage is provided,
     * it will be set to the responseMessage.
     */
    public Optional<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * The userId from the client's token
     */
    public Optional<String> getUserId() {
        return userId;
    }

    /**
     * The deviceId given by the client
     */
    public Optional<String> getDeviceId() {
        return deviceId;
    }

    /**
     * The ipAddress given by the client
     */
    public Optional<String> getIpAddress() {
        return ipAddress;
    }

    @Override
    public String getMessage() {
        return this.errorMessage.orElse("No Error Message");
    }

    @Override
    public String getLocalizedMessage() {
        return this.errorMessage.orElse("No Error Message");
    }

    /**
     * Generates a log message used for backend logging
     */
    public String getLogMessage() {
        StringBuilder logMessageBuilder = new StringBuilder();
        logMessageBuilder.append(getStrFromResponseStatus(this.responseStatus));
        logMessageBuilder.append(" | Calling Class: ");
        logMessageBuilder.append(this.callingClass.getSimpleName());

        if (this.errorMessage.isPresent()) {
            logMessageBuilder.append(" | Error Message: ");
            logMessageBuilder.append(this.errorMessage.get());
        }

        if (this.userId.isPresent()) {
            logMessageBuilder.append(" | User ID: ");
            logMessageBuilder.append(this.userId.get());
        }

        if (this.deviceId.isPresent()) {
            logMessageBuilder.append(" | Device ID: ");
            logMessageBuilder.append(this.deviceId.get());
        }

        if (this.ipAddress.isPresent()) {
            logMessageBuilder.append(" | IP Address: ");
            logMessageBuilder.append(this.ipAddress.get());
        }

        return logMessageBuilder.toString();
    }

    public static class Builder {
        private final ResponseStatusCode responseStatus;
        private final Class<?> callingClass;
        private Optional<String> responseMessage = Optional.empty();
        private Optional<String> errorMessage = Optional.empty();
        private Optional<String> userId = Optional.empty();
        private Optional<String> deviceId = Optional.empty();
        private Optional<String> ipAddress = Optional.empty();

        public Builder(ResponseStatusCode responseStatus, Class<?> callingClass) {
            this.responseStatus = responseStatus;
            this.callingClass = callingClass;
        }

        public Builder withResponseMessage(String responseMessage) {
            this.responseMessage = Optional.of(responseMessage);
            return this;
        }

        /**
         * Gives a standard internal error response back to the client
         */
        public Builder withInternalErrorResponseMessage() {
            this.responseMessage = Optional.of("Sorry, " +
                    "something went wrong on our end. Please try again.");
            return this;
        }

        /**
         * Gives a standard unauthorized response back to the client
         */
        public Builder withUnauthorizedResponseMessage() {
            this.responseMessage = Optional.of("Given token is invalid.");
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = Optional.of(errorMessage);
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = Optional.of(userId);
            return this;
        }

        public Builder withDeviceId(String deviceId) {
            this.deviceId = Optional.of(deviceId);
            return this;
        }

        public Builder withIpAddress(HttpServletRequest servletRequest) {
            this.ipAddress = Optional.of(servletRequest
                    .getAttribute(CLIENT_IP_ATTR).toString());
            return this;
        }

        public Builder withIpAddress(String ipAddressStr) {
            try {
                IPAddressString ipAddress = new IPAddressString(ipAddressStr);
                ipAddress.validate();
                this.ipAddress = Optional.of(ipAddressStr);
                return this;
            } catch (AddressStringException ex) {
                throw new RuntimeException(String.format(
                        "IP Address '%s' is an invalid address", ipAddressStr));
            }
        }

        public ControllerException build() {
            return new ControllerException(this);
        }
    }
}
