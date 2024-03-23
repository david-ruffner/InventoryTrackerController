package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.controller.responses.ControllerExceptionResponse;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import com.davidruffner.inventorytrackercontroller.util.Encryption;
import com.davidruffner.inventorytrackercontroller.util.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthControllerAdvice {
    private static Logging LOGGER;

    @Autowired
    UserService userService;

    @Autowired
    Encryption encryption;

    @ExceptionHandler(value = ControllerException.class)
    public ResponseEntity<String> handleControllerException(ControllerException ex) {
        LOGGER = new Logging(ex.getCallingClass());
        LOGGER.error(ex.getLogMessage());

        return new ControllerExceptionResponse(ex).getJSONResponse();
    }
}
