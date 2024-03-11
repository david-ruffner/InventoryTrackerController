package com.davidruffner.inventorytrackercontroller.exceptions;

import com.davidruffner.inventorytrackercontroller.util.Logging;

import java.util.Optional;

public class BadRequestException extends Exception {
    private final Logging LOGGER;
    private final Class<?> callingClass;
//    logMessage is used for internal logging, responseMessage is what actually gets returned to the client
    private final String logMessage;
    private final String responseMessage;
    private final Optional<String> ipAddress;

    private BadRequestException(Builder builder) {
        this.callingClass = builder.callingClass;
        this.logMessage = builder.logTemplate.toString();
        this.responseMessage = builder.message;
        this.ipAddress = builder.ipAddress;

        LOGGER = new Logging(builder.callingClass);
        LOGGER.error(this.logMessage);
    }

    @Override
    public String getMessage() {
        return this.logMessage;
    }

    public Class<?> getCallingClass() {
        return callingClass;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public Optional<String> getIpAddress() {
        return ipAddress;
    }

    public static class Builder {
        private Class<?> callingClass;
        private StringBuilder logTemplate = new StringBuilder();
        private String message;
        private Optional<String> ipAddress = Optional.empty();

        public Builder(Class<?> callingClass, String message) {
            this.callingClass = callingClass;
            this.message = message;

            logTemplate.append(String.format("BAD_REQUEST | Calling Class: %s | Error: %s",
                    this.callingClass.getName(),
                    this.message));
        }

        public Builder setIpAddress(String ipAddress) {
            this.ipAddress = Optional.of(ipAddress);
            return this;
        }

        public BadRequestException build() {
            this.ipAddress.ifPresent(add -> logTemplate.append(String.format(
                    " | IP Address: %s", add)));

            return new BadRequestException(this);
        }
    }
}
