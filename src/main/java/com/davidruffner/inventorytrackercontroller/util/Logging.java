package com.davidruffner.inventorytrackercontroller.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logging {
    private final Class<?> className;
    private final Logger LOGGER;

    public Logging(Class<?> className) {
        this.className = className;
        this.LOGGER = LoggerFactory.getLogger(this.className);
    }

    public void error(String errMsg) {
        this.LOGGER.error(String.format("ERROR | Class: %s | Error Message: %s",
                this.className, errMsg));
    }

    public void error(String userId, String errMsg) {
        this.LOGGER.error(String.format("ERROR | Class: %s | User ID: %s |" +
                " Error Message: %s", this.className, userId, errMsg));
    }

    public void info(String infoMsg) {
        this.LOGGER.info(String.format("INFO | Class: %s | Message: %s",
                this.className, infoMsg));
    }

    public void info(String userId, String infoMsg) {
        this.LOGGER.info(String.format("INFO | Class: %s | User ID: %s | Message: %s",
                this.className, userId, infoMsg));
    }
}
