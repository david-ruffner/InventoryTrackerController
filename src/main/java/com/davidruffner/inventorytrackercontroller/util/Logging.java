package com.davidruffner.inventorytrackercontroller.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.util.Logging.LogLevel.*;

public class Logging {
    public enum LogLevel {
        AUTH_ERROR,
        ERROR,
        WARN,
        INFO,
        DEBUG
    }

    private static final Map<LogLevel, String> logLevelStrMap;
    static {
        logLevelStrMap = Map.ofEntries(
                Map.entry(AUTH_ERROR, "AUTH_ERROR"),
                Map.entry(ERROR, "ERROR"),
                Map.entry(WARN, "WARN"),
                Map.entry(INFO, "INFO"),
                Map.entry(DEBUG, "DEBUG")
        );
    }

    public static LogLevel getLogLevelFromStr(String logLevelStr) throws RuntimeException {
        try {
            return logLevelStrMap.entrySet().stream().filter(logLevel ->
                    logLevel.getValue().equals(logLevelStr)).toList().getFirst().getKey();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static String getStrFromLogLevel(LogLevel logLevel) throws RuntimeException {
        try {
            return logLevelStrMap.get(logLevel);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private final Class<?> className;
    private final Logger LOGGER;

    public Logging(Class<?> className) {
        this.className = className;
        this.LOGGER = LoggerFactory.getLogger(this.className);
    }

    public void authError(String userId, String errMsg) {
        this.LOGGER.error(String.format("%s | Class: %s | User ID: %s |" +
                " Error Message: %s", getStrFromLogLevel(AUTH_ERROR), this.className, userId, errMsg));
    }

    public void error(String errMsg) {
        this.LOGGER.error(String.format("%s | Class: %s | Error Message: %s",
                getStrFromLogLevel(ERROR), this.className, errMsg));
    }

    public static class ErrorLogBuilder {
        private final String errMsg;
        private final Class<?> className;
        private final Logger LOGGER;
        private Optional<String> userId = Optional.empty();
        private Optional<String> deviceId = Optional.empty();
        private Optional<String> ipAddress = Optional.empty();

        private ErrorLogBuilder(String errMsg, Class<?> className) {
            this.errMsg = errMsg;
            this.className = className;
            this.LOGGER = LoggerFactory.getLogger(this.className);
        }

        public static ErrorLogBuilder newErrorLogBuilder(String errMsg, Class<?> className) {
            return new ErrorLogBuilder(errMsg, className);
        }

        public ErrorLogBuilder withUserId(String userId) {
            this.userId = Optional.of(userId);
            return this;
        }

        public ErrorLogBuilder withDeviceId(String deviceId) {
            this.deviceId = Optional.of(deviceId);
            return this;
        }

        public ErrorLogBuilder withIpAddress(String ipAddress) {
            this.ipAddress = Optional.of(ipAddress);
            return this;
        }

        public void log() {
            StringBuilder logBuilder = new StringBuilder(String.format(
                    "%s | Class: %s | Error Msg: %s", getStrFromLogLevel(ERROR),
                    this.className, this.errMsg));
            this.userId.ifPresent(userId -> logBuilder.append(
                    String.format(" | User ID: %s", userId)));
            this.deviceId.ifPresent(deviceId -> logBuilder.append(
                    String.format(" | Device ID: %s", deviceId)));
            this.ipAddress.ifPresent(ipAddress -> logBuilder.append(
                    String.format(" | IP Address: %s", ipAddress)));

            this.LOGGER.error(logBuilder.toString());
        }
    }

    public void warn(String errMsg) {
        this.LOGGER.error(String.format("%s | Class: %s | Error Message: %s",
                getStrFromLogLevel(WARN), this.className, errMsg));
    }

    public void warn(String userId, String errMsg) {
        this.LOGGER.error(String.format("%s | Class: %s | User ID: %s |" +
                " Error Message: %s", getStrFromLogLevel(WARN), this.className, userId, errMsg));
    }

    public void info(String errMsg) {
        this.LOGGER.error(String.format("%s | Class: %s | Error Message: %s",
                getStrFromLogLevel(INFO), this.className, errMsg));
    }

    public void info(String userId, String errMsg) {
        this.LOGGER.error(String.format("%s | Class: %s | User ID: %s |" +
                " Error Message: %s", getStrFromLogLevel(INFO), this.className, userId, errMsg));
    }

    public void debug(String errMsg) {
        this.LOGGER.error(String.format("%s | Class: %s | Error Message: %s",
                getStrFromLogLevel(DEBUG), this.className, errMsg));
    }

    public void debug(String userId, String errMsg) {
        this.LOGGER.error(String.format("%s | Class: %s | User ID: %s |" +
                " Error Message: %s", getStrFromLogLevel(DEBUG), this.className, userId, errMsg));
    }
}
