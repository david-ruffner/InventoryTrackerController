package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.DEVICE_NOT_AUTHORIZED;
import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.USER_NOT_AUTHORIZED;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    AuthResponse.Builder authResponseBuilder;

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestParam("username") String username,
                                           @RequestParam("password") String password,
                                           @RequestParam("device_id") String device_id) {
        Optional<User> userOpt = userService.getUser(username, password);
        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(401)
                    .body(authResponseBuilder.buildErrorResponse(USER_NOT_AUTHORIZED).toString());
        }

        User user = userOpt.get();
        if (!userService.isUserDeviceAuthorized(user, device_id)) {
            return ResponseEntity
                    .status(403)
                    .body(authResponseBuilder.buildErrorResponse(DEVICE_NOT_AUTHORIZED).toString());
        }

        return ResponseEntity
                .ok()
                .body(authResponseBuilder.buildSuccessResponse(device_id, user).toString());
    }
}
