package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode;
import com.davidruffner.inventorytrackercontroller.util.Logging;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.getResponseStatusFromStr;

public abstract class BaseResponseBuilder {
    private static final Logging LOGGER = new Logging(BaseResponseBuilder.class);

    protected ResponseStatusCode responseStatus;

    public BaseResponseBuilder setResponseStatus(ResponseStatusCode responseStatus) {
        this.responseStatus = responseStatus;
        return this;
    }

    public void addJSONParentNode(JsonNode rootNode) throws RuntimeException {
        try {
            if (rootNode.has("responseStatus"))
                this.responseStatus = getResponseStatusFromStr(rootNode.get("responseStatus").asText());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    protected abstract BaseResponse buildResponseFromJSON(String jsonStr) throws RuntimeException;

    public abstract BaseResponse buildResponseObj();

    public abstract ResponseEntity<String> buildResponseEntity();

    protected void validate() throws RuntimeException {
        if (null == this.responseStatus) {
            LOGGER.error("Tried to build a Response Message without setting " +
                    "responseStatus");
            throw new RuntimeException("Tried to build a Response Message without setting " +
                    "responseStatus");
        }

        validateChild();
    }

    protected abstract void validateChild() throws RuntimeException;

    protected boolean isStrEmpty(String str) {
        return (null == str || str.isEmpty());
    }

    protected boolean isStrEmpty(Optional<String> str) {
        return str.filter(this::isStrEmpty).isPresent();
    }
}
