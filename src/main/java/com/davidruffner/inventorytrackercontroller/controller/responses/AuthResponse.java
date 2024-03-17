package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.auth0.jwt.JWT;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import com.davidruffner.inventorytrackercontroller.util.Encryption;
import com.davidruffner.inventorytrackercontroller.util.Logging;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.SUCCESS;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.getHttpStatusFromResponseStatus;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.getResponseStatusFromStr;
import static com.davidruffner.inventorytrackercontroller.util.Constants.*;

public class AuthResponse extends BaseResponse {
    private static final Logging LOGGER = new Logging(AuthResponse.class);

    private final Optional<String> message;
    private final Optional<String> token;
    private final Optional<String> displayName;

    private AuthResponse(Builder builder) {
        super(builder.responseStatus);
        this.message = builder.message;
        this.token = builder.token;
        this.displayName = builder.displayName;
    }

    public Optional<String> getMessage() {
        return message;
    }

    public Optional<String> getToken() {
        return token;
    }

    public Optional<String> getDisplayName() {
        return displayName;
    }

    @Override
    public void addJSONChildNodes(ObjectNode rootNode) throws RuntimeException {
        this.message.ifPresent(s -> rootNode.put("message", s));
        this.token.ifPresent(token -> rootNode.put("token", token));
        this.displayName.ifPresent(name -> rootNode.put("displayName", name));
    }

    @Component(AUTH_RESPONSE_BUILDER_BEAN)
    @Scope("prototype")
    public static class Builder extends BaseResponseBuilder {
        @Autowired
        Encryption encryption;

        @Autowired
        UserService userService;

        private Optional<String> message = Optional.empty();
        private Optional<String> token = Optional.empty();
        private Optional<String> displayName = Optional.empty();

        public Builder setMessage(String message) {
            this.message = Optional.of(message);
            return this;
        }

        public Builder setToken(String token) {
            this.token = Optional.of(token);
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = Optional.of(displayName);
            return this;
        }

        @Override
        public void validateChild() throws RuntimeException {
            if (this.responseStatus.equals(SUCCESS)) {
                if (isStrEmpty(this.token)) {
                    LOGGER.error("Tried to build successful AuthResponse " +
                            "without token being set");
                    throw new RuntimeException("Tried to build successful AuthResponse " +
                            "without token being set");
                } else if (isStrEmpty(this.displayName)) {
                    LOGGER.error("Tried to build successful AuthResponse " +
                            "without displayName being set");
                    throw new RuntimeException("Tried to build successful AuthResponse " +
                            "without displayName being set");
                }

            } else {
                if (isStrEmpty(this.message)) {
                    LOGGER.error("Tried to build non-successful AuthResponse " +
                            "without message being set");
                    throw new RuntimeException("Tried to build non-successful AuthResponse " +
                            "without message being set");
                }
            }
        }

        @Override
        public BaseResponse buildResponseObj() {
            validate();
            return new AuthResponse(this);
        }

        @Override
        public ResponseEntity<String> buildResponseEntity() {
            validate();
            return new ResponseEntity<>(new AuthResponse(this).toJSONString(),
                    getHttpStatusFromResponseStatus(this.responseStatus));
        }

        @Override
        public AuthResponse buildResponseFromJSON(String jsonStr) throws RuntimeException {
            ObjectMapper mapper = new ObjectMapper();

            try {
                JsonNode jsonNode = mapper.readTree(jsonStr);
                addJSONParentNode(jsonNode);

                if (jsonNode.has("message"))
                    this.message = Optional.of(jsonNode.get("message").asText());
                if (jsonNode.has("token"))
                    this.token = Optional.of(jsonNode.get("token").asText());
                if (jsonNode.has("displayName"))
                    this.displayName = Optional.of(jsonNode.get("displayName").asText());

                validate();
                return new AuthResponse(this);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                throw new RuntimeException(ex.getMessage());
            }
        }

        private void createSuccessResponse(String deviceId, User user) {
            // If first name is not unique, abbreviate with last initial
            this.displayName = Optional.of(userService.isFirstNameUnique(user.getFirstName()) ?
                    user.getFirstName() : String.format("%s %s.", user.getFirstName(),
                    String.valueOf(user.getLastName().charAt(0)).toUpperCase()));

            // Build JWT
            String jwtToken = JWT.create()
                    .withClaim(DEVICE_ID_JWT_CLAIM, deviceId)
                    .withClaim(USERNAME_JWT_CLAIM, user.getUserId())
                    .withClaim(DISPLAY_NAME_JWT_CLAIM, this.displayName.get())
                    .withNotBefore(Instant.now())
                    .withExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                    .sign(encryption.getJWTAlgorithm());

            // Encrypt token
            this.token = Optional.of(encryption.encryptToAES(jwtToken));
            this.responseStatus = SUCCESS;
            validate();
        }

        public ResponseEntity<String> buildSuccessResponseEntity(String deviceId, User user) {
            createSuccessResponse(deviceId, user);
            return new ResponseEntity<>(new AuthResponse(this).toJSONString(), HttpStatus.OK);
        }

        public AuthResponse buildSuccessResponseObj(String deviceId, User user) {
            createSuccessResponse(deviceId, user);
            return new AuthResponse(this);
        }

        private void createAuthExResponse(AuthException ex) {
            this.responseStatus = ex.getAuthStatus();
            this.message = ex.getResponseMessage();
            validate();
        }

        public ResponseEntity<String> buildAuthExResponseEntity(AuthException ex) {
            createAuthExResponse(ex);
            AuthResponse authResponse = new AuthResponse(this);
            return new ResponseEntity<>(authResponse.toJSONString(), authResponse.getHttpStatus());
        }

        public AuthResponse buildAuthExResponseObj(AuthException ex) {
            createAuthExResponse(ex);
            return new AuthResponse(this);
        }
    }
}
