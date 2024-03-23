package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode;
import com.davidruffner.inventorytrackercontroller.util.Logging;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.*;

public abstract class BaseResponse {
    private static final Logging LOGGER = new Logging(BaseResponse.class);

    protected final ResponseStatusCode responseStatus;

    public BaseResponse(ResponseStatusCode responseStatus) {
        this.responseStatus = responseStatus;
    }

    public ResponseStatusCode getResponseStatus() {
        return responseStatus;
    }

    public int getHttpStatusInt() {
        return getHttpStatusIntFromResponseStatus(this.responseStatus);
    }

    public HttpStatus getHttpStatus() {
        return getHttpStatusFromResponseStatus(this.responseStatus);
    }

    protected abstract void addJSONChildNodes(ObjectNode rootNode) throws RuntimeException;

    public ResponseEntity<String> getJSONResponse() throws RuntimeException {
        return new ResponseEntity<>(toJSONString(), getHttpStatusFromResponseStatus(this.responseStatus));
    }

    private String toJSONString() throws RuntimeException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        rootNode.put("responseStatus", getStrFromResponseStatus(this.responseStatus));
        addJSONChildNodes(rootNode);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    protected boolean isStrEmpty(String str) {
        return (null == str || str.isEmpty());
    }

    protected boolean isStrEmpty(Optional<String> str) {
        return str.filter(this::isStrEmpty).isPresent();
    }
}
