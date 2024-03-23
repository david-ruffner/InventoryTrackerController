package com.davidruffner.inventorytrackercontroller.controller.responses;

import com.auth0.jwt.JWT;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.util.Encryption;
import com.davidruffner.inventorytrackercontroller.util.Logging;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.SUCCESS;
import static com.davidruffner.inventorytrackercontroller.util.Constants.*;

public class AuthResponse extends BaseResponse {
    private static final Logging LOGGER = new Logging(AuthResponse.class);

    private final Encryption encryption;
    private final UserService userService;
    private final String token;
    private final String displayName;

    public AuthResponse(Encryption encryption, UserService userService, String deviceId, User user) {
        super(SUCCESS);
        this.encryption = encryption;
        this.userService = userService;

        // If first name is not unique, abbreviate with last initial
        this.displayName = this.userService.isFirstNameUnique(user.getFirstName()) ?
                user.getFirstName() : String.format("%s %s.", user.getFirstName(),
                String.valueOf(user.getLastName().charAt(0)).toUpperCase());

        // Build JWT
        String jwtToken = JWT.create()
                .withClaim(DEVICE_ID_JWT_CLAIM, deviceId)
                .withClaim(USERNAME_JWT_CLAIM, user.getUserId())
                .withClaim(DISPLAY_NAME_JWT_CLAIM, this.displayName)
                .withNotBefore(Instant.now())
                .withExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .sign(encryption.getJWTAlgorithm());

        // Encrypt token
        this.token = this.encryption.encryptToAES(jwtToken);
    }

    public String getToken() {
        return token;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void addJSONChildNodes(ObjectNode rootNode) throws RuntimeException {
        rootNode.put("token", this.token);
        rootNode.put("displayName", this.displayName);
    }
}
