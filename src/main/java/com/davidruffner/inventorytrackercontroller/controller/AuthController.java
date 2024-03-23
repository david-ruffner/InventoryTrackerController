package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.controller.requests.AddIPAddressRequest;
import com.davidruffner.inventorytrackercontroller.controller.requests.AuthRequest;
import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import com.davidruffner.inventorytrackercontroller.util.Encryption;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.DEVICE_NOT_AUTHORIZED;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.USER_NOT_AUTHORIZED;
import static com.davidruffner.inventorytrackercontroller.util.Constants.USER_ATTR;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    Encryption encryption;

    @Autowired
    UserService userService;

    @PostMapping("/addAuthorizedAddresses")
    public String addAuthorizedAddresses(@RequestBody AddIPAddressRequest requestBody,
                                         HttpServletRequest servletRequest) throws Exception {
        User user = (User) servletRequest.getAttribute(USER_ATTR);
        return user.getFirstName();
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody AuthRequest authRequest,
                                               HttpServletRequest servletRequest) throws ControllerException {
        Optional<User> userOpt = userService.getUser(authRequest.getUsername(), authRequest.getPassword());

        if (userOpt.isEmpty()) {
            throw new ControllerException.Builder(USER_NOT_AUTHORIZED, this.getClass())
                    .withErrorMessage(String.format("Username '%s' is not authorized",
                            authRequest.getUsername()))
                    .withUnauthorizedResponseMessage()
                    .withIpAddress(servletRequest)
                    .build();
        }

        User user = userOpt.get();
        if (!userService.isUserDeviceAuthorized(user, authRequest.getDevice_id())) {
            throw new ControllerException.Builder(DEVICE_NOT_AUTHORIZED, this.getClass())
                    .withErrorMessage(String.format("Device with ID '%s' is not authorized",
                            authRequest.getDevice_id()))
                    .withUnauthorizedResponseMessage()
                    .withIpAddress(servletRequest)
                    .build();
        }

        return new AuthResponse(encryption, userService,
                authRequest.getDevice_id(), user).getJSONResponse();
    }
}
