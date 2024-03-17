package com.davidruffner.inventorytrackercontroller.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

    public void error(String userId, String errMsg) {
        this.LOGGER.error(String.format("%s | Class: %s | User ID: %s |" +
                " Error Message: %s", getStrFromLogLevel(ERROR), this.className, userId, errMsg));
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
