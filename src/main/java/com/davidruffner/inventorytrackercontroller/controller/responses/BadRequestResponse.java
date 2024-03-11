package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.davidruffner.inventorytrackercontroller.controller.requests.BaseRequest;
import com.davidruffner.inventorytrackercontroller.controller.requests.BaseRequest.OneOfField;
import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = BadRequestResponse.JsonFilter.class)
@JsonSerialize()
public class BadRequestResponse {
    public static class JsonFilter {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Optional<?>) {
                return ((Optional<?>) obj).isEmpty();
            } else {
                return false;
            }
        }
    }

    public static class MissingField {
        String requirement
    }

    private final String status = "BAD_REQUEST";
    private String message;
    private Optional<Map<String, List<OneOfField>>> missingOneOfFieldsMap;
    private Optional<List<String>> missingRequiredFieldsList;
    private

    public BadRequestResponse(BadRequestException ex) {
        this.message = ex.getResponseMessage();
        this.missingOneOfFieldsMap = ex.getMissingOneOfFieldsMap();
        this.missingRequiredFieldsList = ex.getMissingRequiredFieldsList();
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Optional<Map<String, List<OneOfField>>> getMissingOneOfFieldsMap() {
        return missingOneOfFieldsMap;
    }

    public Optional<List<String>> getMissingRequiredFieldsList() {
        return missingRequiredFieldsList;
    }
}
