package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize()
public class BadRequestResponse {
    private final String status = "BAD_REQUEST";
    private String message;

    public BadRequestResponse(BadRequestException ex) {
        this.message = ex.getResponseMessage();
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
