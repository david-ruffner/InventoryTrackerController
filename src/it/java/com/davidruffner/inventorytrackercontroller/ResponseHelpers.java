package com.davidruffner.inventorytrackercontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseHelpers {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T getControllerExResponse(String jsonStr, Class<T> clazz)
            throws JsonProcessingException {
        return mapper.readValue(jsonStr, clazz);
    }
}
