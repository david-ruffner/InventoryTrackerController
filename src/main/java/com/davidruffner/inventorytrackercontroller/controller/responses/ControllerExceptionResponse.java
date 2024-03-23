package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public class ControllerExceptionResponse extends BaseResponse {
    /**
     * message here is the responseMessage from ControllerException
     */
    private final Optional<String> message;

    public ControllerExceptionResponse(ControllerException ex) {
        super(ex.getResponseStatus());
        this.message = ex.getResponseMessage();
    }

    public Optional<String> getMessage() {
        return message;
    }

    @Override
    protected void addJSONChildNodes(ObjectNode rootNode) throws RuntimeException {
        this.message.ifPresent(msg -> rootNode.put("message", msg));
    }
}
