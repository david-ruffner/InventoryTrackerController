package com.davidruffner.inventorytrackercontroller.responseHelpers;

import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus;
import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode;

public class ResponseHelperBase {
    private final ResponseStatusCode responseStatus;

    public ResponseHelperBase(String responseStatus) {
        this.responseStatus = ResponseStatus.getResponseStatusFromStr(responseStatus);
    }

    public ResponseStatusCode getResponseStatus() {
        return responseStatus;
    }
}
