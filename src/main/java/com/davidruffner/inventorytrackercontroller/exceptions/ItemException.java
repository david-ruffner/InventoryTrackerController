package com.davidruffner.inventorytrackercontroller.exceptions;

import com.davidruffner.inventorytrackercontroller.util.Logging;
import org.slf4j.LoggerFactory;

public class ItemException extends Exception {
    private final Logging LOGGER;

    public ItemException(String message, Class<?> callingClass) {
        super(message);
        LOGGER = new Logging(callingClass);
        LOGGER.error(message);
    }

    public ItemException(String userId, String message, Class<?> callingClass) {
        super(message);
        LOGGER = new Logging(callingClass);
        LOGGER.error(userId, message);
    }
}
