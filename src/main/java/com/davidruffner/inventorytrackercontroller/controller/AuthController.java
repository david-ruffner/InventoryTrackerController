package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public static class AuthRequest {
        private String username;
        private String password;
        private String device_id;

        public AuthRequest(String username, String password, String device_id) {
            this.username = username;
            this.password = password;
            this.device_id = device_id;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getDevice_id() {
            return device_id;
        }
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody AuthRequest authRequest) {
        Optional<User> userOpt = userService.getUser(authRequest.getUsername(), authRequest.getPassword());
        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(401)
                    .body(authResponseBuilder.buildErrorResponse(USER_NOT_AUTHORIZED).toString());
        }

        User user = userOpt.get();
        if (!userService.isUserDeviceAuthorized(user, authRequest.getDevice_id())) {
            return ResponseEntity
                    .status(403)
                    .body(authResponseBuilder.buildErrorResponse(DEVICE_NOT_AUTHORIZED).toString());
        }

        return ResponseEntity
                .ok()
                .body(authResponseBuilder.buildSuccessResponse(authRequest.getDevice_id(), user).toString());
    }
}
