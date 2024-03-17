package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize()
public class BadRequestResponse extends BaseResponse {
    private final String status = "BAD_REQUEST";
    private String message;

    public BadRequestResponse(BadRequestException ex) {
        this.message = ex.getResponseMessage();
    }

    public BadRequestResponse(String jsonStr) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonStr);

        if (jsonNode.has("message")) {
            this.message = jsonNode.get("message").asText();
        } else {
            throw new RuntimeException("Tried to create BadRequestResponse with no message");
        }
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
