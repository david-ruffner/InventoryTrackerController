package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.auth0.jwt.JWT;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.util.Encryption;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.SUCCESS;
import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.USER_NOT_AUTHORIZED;

public class AuthResponse {
    public enum AuthStatus {
        SUCCESS,
        USER_NOT_AUTHORIZED,
        DEVICE_NOT_AUTHORIZED
    }

    private AuthStatus authStatus;
    private String token;
    private String displayName;

    public AuthStatus getAuthStatus() {
        return authStatus;
    }

    public String getToken() {
        return token;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AuthResponse(AuthStatus authStatus) {
        this.authStatus = authStatus;
        this.token = "";
        this.displayName = "";
    }

    public AuthResponse(AuthStatus authStatus, String token,
                        String displayName) {
        this.authStatus = authStatus;
        this.token = token;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Component
    public static class Builder {
        @Autowired
        Encryption encryption;

        @Autowired
        UserService userService;

        public AuthResponse buildErrorResponse(AuthStatus authStatus) {
            return new AuthResponse(authStatus);
        }

        public AuthResponse buildSuccessResponse(String deviceId, User user) {
            // If first name is not unique, abbreviate with last initial
            String displayName = userService.isFirstNameUnique(user.getFirstName()) ?
                    user.getFirstName() : String.format("%s %s.", user.getFirstName(),
                    String.valueOf(user.getLastName().charAt(0)).toUpperCase());

            // Build JWT
            String jwtToken = JWT.create()
                    .withClaim("device-id", deviceId)
                    .withClaim("username", user.getUserId())
                    .withClaim("display-name", displayName)
                    .withNotBefore(Instant.now())
                    .withExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                    .sign(encryption.getJWTAlgorithm());

            // Encrypt token
            String token = encryption.encryptToAES(jwtToken);

            return new AuthResponse(SUCCESS, token, displayName);
        }
    }
}
