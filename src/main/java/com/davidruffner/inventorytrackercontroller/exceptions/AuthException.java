package com.davidruffner.inventorytrackercontroller.exceptions;

import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode;
import com.davidruffner.inventorytrackercontroller.util.Logging;

import java.util.Optional;

public class AuthException extends Exception {
    private final Logging LOGGER;
    private final ResponseStatusCode authStatus;
    private final Class<?> callingClass;
//    logMessage is used for internal logging, responseMessage is what actually gets returned to the client
    private final String logMessage;
    private final Optional<String> responseMessage;
    private final Optional<String> userId;
    private final Optional<String> ipAddress;

    /*
    This is probably stupid but I thought it was funny
    When a request is sent with an invalid IP address all the client
    will get back is "Flagrant System Error: Not a chance LOL".
    This will be denoted by the NOT_A_CHANCE AuthStatus
    */
    private AuthException(Builder builder) {
        this.authStatus = builder.authStatus;
        this.callingClass = builder.callingClass;
        this.logMessage = builder.logTemplate.toString();
        this.responseMessage = builder.message;
        this.userId = builder.userId;
        this.ipAddress = builder.ipAddress;

        LOGGER = new Logging(builder.callingClass);
        LOGGER.error(this.logMessage);
    }

    @Override
    public String getMessage() {
        return this.logMessage;
    }

    public ResponseStatusCode getAuthStatus() {
        return authStatus;
    }

    public Class<?> getCallingClass() {
        return callingClass;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public Optional<String> getResponseMessage() {
        return responseMessage;
    }

    public Optional<String> getUserId() {
        return userId;
    }

    public Optional<String> getIpAddress() {
        return ipAddress;
    }

    public static class Builder {
        private ResponseStatusCode authStatus;
        private Class<?> callingClass;
        private StringBuilder logTemplate = new StringBuilder();
        private Optional<String> message = Optional.empty();
        private Optional<String> userId = Optional.empty();
        private Optional<String> ipAddress = Optional.empty();

        public Builder(ResponseStatusCode authStatus, Class<?> callingClass) {
            this.authStatus = authStatus;
            this.callingClass = callingClass;
            logTemplate.append(String.format("AUTH_ERROR | AuthStatus: %s | Calling Class: %s",
                    this.authStatus, this.callingClass.getName()));
        }

        public Builder setMessage(String message) {
            this.message = Optional.of(message);
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = Optional.of(userId);
            return this;
        }

        public Builder setIpAddress(String ipAddress) {
            this.ipAddress = Optional.of(ipAddress);
            return this;
        }

        public AuthException build() {
            this.message.ifPresent(msg -> logTemplate.append(String.format(
                    " | Message: %s", msg)));
            this.userId.ifPresent(id -> logTemplate.append(String.format(
                    " | User ID: %s", id)));
            this.ipAddress.ifPresent(add -> logTemplate.append(String.format(
                    " | IP Address: %s", add)));

            return new AuthException(this);
        }
    }
}
