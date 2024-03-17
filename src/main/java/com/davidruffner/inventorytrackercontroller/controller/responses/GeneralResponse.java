package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.davidruffner.inventorytrackercontroller.util.Constants;
import com.davidruffner.inventorytrackercontroller.util.Logging;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.getHttpStatusFromResponseStatus;

public class GeneralResponse extends BaseResponse {
    private static final Logging LOGGER = new Logging(GeneralResponse.class);

    private final Optional<String> message;

    private GeneralResponse(GeneralResponse.Builder builder) {
        super(builder.responseStatus);
        this.message = builder.message;
    }

    public Optional<String> getMessage() {
        return message;
    }

    @Override
    protected void addJSONChildNodes(ObjectNode rootNode) throws RuntimeException {
        this.message.ifPresent(msg -> rootNode.put("message", msg));
    }

    @Component(value = Constants.GENERAL_RESPONSE_BUILDER_BEAN)
    @Scope("prototype")
    public static class Builder extends BaseResponseBuilder {
        private Optional<String> message = Optional.empty();

        public Builder setMessage(Optional<String> message) {
            this.message = message;
            return this;
        }

        @Override
        public BaseResponse buildResponseFromJSON(String jsonStr) throws RuntimeException {
            ObjectMapper mapper = new ObjectMapper();

            try {
                JsonNode jsonNode = mapper.readTree(jsonStr);
                addJSONParentNode(jsonNode);

                if (jsonNode.has("message"))
                    this.message = Optional.of(jsonNode.get("message").asText());

                validate();
                return new GeneralResponse(this);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                throw new RuntimeException(ex.getMessage());
            }
        }

        @Override
        public BaseResponse buildResponseObj() {
            validate();
            return new GeneralResponse(this);
        }

        @Override
        public ResponseEntity<String> buildResponseEntity() {
            validate();
            return new ResponseEntity<>(new GeneralResponse(this).toJSONString(),
                    getHttpStatusFromResponseStatus(this.responseStatus));
        }

        @Override
        protected void validateChild() throws RuntimeException {
            // Intentionally empty
        }
    }
}
