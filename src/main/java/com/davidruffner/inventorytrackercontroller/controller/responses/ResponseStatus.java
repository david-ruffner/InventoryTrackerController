package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.davidruffner.inventorytrackercontroller.util.Logging;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.*;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.BAD_REQUEST;
import static org.springframework.http.HttpStatus.*;

public class ResponseStatus {
    private static final Logging LOGGER = new Logging(ResponseStatus.class);

    public enum ResponseStatusCode {
        SUCCESS,
        USER_NOT_AUTHORIZED,
        DEVICE_NOT_AUTHORIZED,
        NOT_A_CHANCE, // Used for unauthorized IP address
        BAD_REQUEST,
        INTERNAL_ERROR
    }

    private static final Map<ResponseStatusCode, HttpStatus> responseStatusToHttpStatusMap;
    static {
        responseStatusToHttpStatusMap = Map.ofEntries(
                Map.entry(SUCCESS, OK),
                Map.entry(USER_NOT_AUTHORIZED, UNAUTHORIZED),
                Map.entry(DEVICE_NOT_AUTHORIZED, FORBIDDEN),
                Map.entry(NOT_A_CHANCE, I_AM_A_TEAPOT),
                Map.entry(BAD_REQUEST, HttpStatus.BAD_REQUEST),
                Map.entry(INTERNAL_ERROR, INTERNAL_SERVER_ERROR)
        );
    }

    private static final Map<ResponseStatusCode, String> responseStatusToStrMap;
    static {
        responseStatusToStrMap = Map.ofEntries(
                Map.entry(SUCCESS, "SUCCESS"),
                Map.entry(USER_NOT_AUTHORIZED, "USER_NOT_AUTHORIZED"),
                Map.entry(DEVICE_NOT_AUTHORIZED, "DEVICE_NOT_AUTHORIZED"),
                Map.entry(NOT_A_CHANCE, "NOT_A_CHANCE"),
                Map.entry(BAD_REQUEST, "BAD_REQUEST"),
                Map.entry(INTERNAL_ERROR, "INTERNAL_ERROR")
        );
    }

    public static ResponseStatusCode getResponseStatusFromHttpStatus(HttpStatus httpStatus) throws RuntimeException {
        try {
            return responseStatusToHttpStatusMap.entrySet().stream().filter(authStatus ->
                    authStatus.getValue().equals(httpStatus)).toList().getFirst().getKey();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static HttpStatus getHttpStatusFromResponseStatus(ResponseStatusCode responseStatusCode) throws RuntimeException {
        try {
            return responseStatusToHttpStatusMap.get(responseStatusCode);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static Integer getHttpStatusIntFromResponseStatus(ResponseStatusCode responseStatusCode) throws RuntimeException {
        try {
            return responseStatusToHttpStatusMap.get(responseStatusCode).value();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static ResponseStatusCode getResponseStatusFromStr(String authStatusStr) throws RuntimeException {
        try {
            return responseStatusToStrMap.entrySet().stream().filter(authStatus ->
                    authStatus.getValue().equals(authStatusStr)).toList().getFirst().getKey();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static String getStrFromResponseStatus(ResponseStatusCode responseStatusCode) throws RuntimeException {
        try {
            return responseStatusToStrMap.get(responseStatusCode);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }
}
