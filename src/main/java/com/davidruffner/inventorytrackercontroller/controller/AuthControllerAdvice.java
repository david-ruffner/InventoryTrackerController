package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.controller.responses.GeneralResponse;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.BAD_REQUEST;
import static com.davidruffner.inventorytrackercontroller.util.Constants.AUTH_RESPONSE_BUILDER_BEAN;
import static com.davidruffner.inventorytrackercontroller.util.Constants.GENERAL_RESPONSE_BUILDER_BEAN;

@ControllerAdvice
public class AuthControllerAdvice {
    @Autowired
    ApplicationContext appContext;

    @ExceptionHandler(value = AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException ex) {
        AuthResponse.Builder authResponseBuilder =
                (AuthResponse.Builder) appContext.getBean(AUTH_RESPONSE_BUILDER_BEAN);
        return authResponseBuilder.buildAuthExResponseEntity(ex);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        GeneralResponse.Builder generalResponseBuilder =
                (GeneralResponse.Builder) appContext.getBean(GENERAL_RESPONSE_BUILDER_BEAN);
        return generalResponseBuilder.setResponseStatus(BAD_REQUEST).buildResponseEntity();
    }
}
