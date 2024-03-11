package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.auth0.jwt.JWT;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import com.davidruffner.inventorytrackercontroller.util.Constants;
import com.davidruffner.inventorytrackercontroller.util.Encryption;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.management.ObjectName;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.*;
import static com.davidruffner.inventorytrackercontroller.util.Constants.*;
import static org.springframework.http.HttpStatus.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AuthResponse.AuthResponseJsonFilter.class)
public class AuthResponse {
    public static class AuthResponseJsonFilter {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Optional<?>) {
                return ((Optional<?>) obj).isEmpty();
            } else {
                return false;
            }
        }
    }

    public enum AuthStatus {
        SUCCESS,
        USER_NOT_AUTHORIZED,
        DEVICE_NOT_AUTHORIZED,
        NOT_A_CHANCE, // Used for unauthorized IP address
        INVALID_IP_ADDRESS
    }

    private static final Map<AuthStatus, HttpStatus> authStatusMap;
    static {
        authStatusMap = Map.ofEntries(
                Map.entry(SUCCESS, OK),
                Map.entry(USER_NOT_AUTHORIZED, UNAUTHORIZED),
                Map.entry(DEVICE_NOT_AUTHORIZED, FORBIDDEN),
                Map.entry(NOT_A_CHANCE, I_AM_A_TEAPOT),
                Map.entry(INVALID_IP_ADDRESS, BAD_REQUEST)
        );
    }

    private static int getHttpCode(AuthStatus authStatus) {
        return authStatusMap.get(authStatus).value();
    }

    private AuthStatus authStatus;
    private Optional<String> message;
    private Optional<String> token = Optional.empty();
    private Optional<String> displayName = Optional.empty();

    public AuthStatus getAuthStatus() {
        return authStatus;
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

    public AuthResponse(AuthException ex) {
        this.authStatus = ex.getAuthStatus();
        this.message = ex.getResponseMessage();
    }

    private AuthResponse(Builder builder) {
        this.authStatus = builder.authStatus;
        this.message = builder.message;
        this.token = builder.token;
        this.displayName = builder.displayName;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("authStatus", this.authStatus.toString());

        this.message.ifPresent(s -> rootNode.put("message", s));
        this.token.ifPresent(token -> rootNode.put("token", token));
        this.displayName.ifPresent(name -> rootNode.put("displayName", name));

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Component(AUTH_RESPONSE_BUILDER_BEAN)
    @Scope("prototype")
    public static class Builder {
        @Autowired
        Encryption encryption;

        @Autowired
        UserService userService;

        private AuthStatus authStatus;
        private Optional<String> message = Optional.empty();
        private Optional<String> token = Optional.empty();
        private Optional<String> displayName = Optional.empty();

        public Builder setAuthStatus(AuthStatus authStatus) {
            this.authStatus = authStatus;
            return this;
        }

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

        public AuthResponse buildErrorResponse(AuthStatus authStatus,
                                               HttpServletResponse servletResponse) {
            this.authStatus = authStatus;
            servletResponse.setStatus(getHttpCode(authStatus));
            return new AuthResponse(this);
        }

        public AuthResponse buildSuccessResponse(String deviceId, User user,
                                                 HttpServletResponse servletResponse) {
            servletResponse.setStatus(getHttpCode(SUCCESS));

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
            this.authStatus = SUCCESS;

            return new AuthResponse(this);
        }
    }
}
