package com.davidruffner.inventorytrackercontroller.responseHelpers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ControllerExResponseHelper extends ResponseHelperBase {
    private final String message;

    @JsonCreator
    public ControllerExResponseHelper(@JsonProperty("responseStatus") String responseStatus,
                                      @JsonProperty("message") String message) {
        super(responseStatus);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
