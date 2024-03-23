package com.davidruffner.inventorytrackercontroller.db.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.repositories.ScannableItemRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import com.davidruffner.inventorytrackercontroller.util.Encryption;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.USER_NOT_AUTHORIZED;
import static com.davidruffner.inventorytrackercontroller.util.Constants.*;

@Service
public class UserService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    ScannableItemRepository scannableItemRepo;

    @Autowired
    Encryption encryptionService;

    public Optional<User> getUser(String username, String password) {
        Optional<User> user = userRepo.findById(username);
        if (user.isEmpty())
            return user;

        if (!DigestUtils.sha256Hex(password).equals(user.get().getSecret()))
            return Optional.empty();

        return user;
    }

    public User getUserFromToken(String token, HttpServletRequest servletRequest)
            throws ControllerException {
        DecodedJWT decodedJWT = encryptionService.getJWTFromToken(token, servletRequest);
        String username = decodedJWT.getClaim(USERNAME_JWT_CLAIM).asString();
        String deviceId = decodedJWT.getClaim(DEVICE_ID_JWT_CLAIM).asString();

        return userRepo.findById(username).orElseThrow(() ->
                new ControllerException.Builder(USER_NOT_AUTHORIZED, this.getClass())
                        .withErrorMessage(String.format(
                                "Username '%s' from token doesn't exist", username))
                        .withUnauthorizedResponseMessage()
                        .withUserId(username)
                        .withDeviceId(deviceId)
                        .withIpAddress(servletRequest)
                        .build());
    }

    public boolean isUserDeviceAuthorized(User user, String device_id) {
        return user.getAuthorizedDevices().stream().anyMatch(device ->
                device.getDeviceId().equals(device_id));
    }

    public boolean isFirstNameUnique(String firstName) {
        return userRepo.getFirstNameCount(firstName) < 2;
    }
}
