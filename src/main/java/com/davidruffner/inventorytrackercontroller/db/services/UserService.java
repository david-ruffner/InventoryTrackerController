package com.davidruffner.inventorytrackercontroller.db.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.repositories.ScannableItemRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import com.davidruffner.inventorytrackercontroller.util.Encryption;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.USER_NOT_AUTHORIZED;
import static com.davidruffner.inventorytrackercontroller.db.services.UserService.UserHelper.getErrorUser;
import static com.davidruffner.inventorytrackercontroller.db.services.UserService.UserHelper.getSuccessUser;
import static com.davidruffner.inventorytrackercontroller.util.Constants.*;

@Service
public class UserService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    ScannableItemRepository scannableItemRepo;

    @Autowired
    Encryption encryptionService;

    public static class UserHelper {
        private final Optional<User> user;
        private final Optional<String> error;

        private UserHelper(Optional<User> user, Optional<String> error) {
            this.user = user;
            this.error = error;
        }

        public static UserHelper getSuccessUser(User user) {
            return new UserHelper(Optional.of(user), Optional.empty());
        }

        public static UserHelper getErrorUser(String errMsg) {
            return new UserHelper(Optional.empty(), Optional.of(errMsg));
        }

        public Optional<User> getUser() {
            return user;
        }

        public Optional<String> getError() {
            return error;
        }
    }

    public UserHelper getUser(String username, String password) {
        Optional<User> user = userRepo.findById(username);
        if (user.isEmpty())
            return getErrorUser(String.format("Username '%s' not found in the system", username));

        if (!DigestUtils.sha256Hex(password).equals(user.get().getSecret()))
            return getErrorUser(String.format("Password for user '%s' was invalid", username));

        return getSuccessUser(user.get());
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

    @Transactional
    public UserHelper getUserWithScannableItems(String username, String password) {
        Optional<User> user = userRepo.findById(username);
        if (user.isEmpty())
            return getErrorUser(String.format("Username '%s' not found in the system", username));

        if (!DigestUtils.sha256Hex(password).equals(user.get().getSecret()))
            return getErrorUser(String.format("Password for user '%s' was invalid", username));

        user.get().getScannableItems().size();
        return getSuccessUser(user.get());
    }
//    public User getUserWithScannableItems(User user) {
//        user.getScannableItems().size();
//        return user;
//    }

    public boolean isUserDeviceAuthorized(User user, String device_id) {
        return user.getAuthorizedDevices().stream().anyMatch(device ->
                device.getDeviceId().equals(device_id));
    }

    public boolean isFirstNameUnique(String firstName) {
        return userRepo.getFirstNameCount(firstName) < 2;
    }
}
