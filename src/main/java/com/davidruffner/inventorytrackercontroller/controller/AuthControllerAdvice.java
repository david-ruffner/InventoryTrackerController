package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.controller.responses.BadRequestResponse;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.NOT_A_CHANCE;
import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
public class AuthControllerAdvice {
    @Autowired
    AuthResponse.Builder responseBuilder;

    @ExceptionHandler(value = AuthException.class)
    public @ResponseBody AuthResponse handleAuthException(HttpServletResponse resp, AuthException ex) {
        if (ex.getAuthStatus().equals(NOT_A_CHANCE)) {
            resp.setStatus(I_AM_A_TEAPOT.value());
            return new AuthResponse(ex);
//            return responseBuilder.setMessage("Flagrant System Error: Not a chance LOL")
//                            .buildErrorResponse(ex.getAuthStatus());
        } else {
            resp.setStatus(UNAUTHORIZED.value());
            return new AuthResponse(ex);
//            return responseBuilder.setMessage(ex.getResponseMessage())
//                    .buildErrorResponse(ex.getAuthStatus());
        }
    }

    @ExceptionHandler(value = BadRequestException.class)
    public @ResponseBody BadRequestResponse handleBadRequest(HttpServletResponse resp,
                                                             BadRequestException ex) {
        resp.setStatus(400);
        return new BadRequestResponse(ex);
    }
}
