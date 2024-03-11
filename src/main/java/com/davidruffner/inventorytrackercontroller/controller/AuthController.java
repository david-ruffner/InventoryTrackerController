package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.controller.requests.AddIPAddressRequest;
import com.davidruffner.inventorytrackercontroller.controller.requests.AuthRequest;
import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import com.davidruffner.inventorytrackercontroller.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.DEVICE_NOT_AUTHORIZED;
import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.USER_NOT_AUTHORIZED;
import static com.davidruffner.inventorytrackercontroller.util.Constants.*;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    ApplicationContext appContext;

    @PostMapping("/addAuthorizedAddresses")
    public String addAuthorizedAddresses(@RequestBody AddIPAddressRequest requestBody,
                                         HttpServletRequest servletRequest) throws Exception {
        User user = (User) servletRequest.getAttribute(USER_ATTR);
        return user.getFirstName();
    }

    @PostMapping("/token")
    public @ResponseBody AuthResponse getToken(@RequestBody AuthRequest authRequest,
                                               HttpServletRequest servletRequest,
                                               HttpServletResponse servletResponse) throws AuthException {
        Optional<User> userOpt = userService.getUser(authRequest.getUsername(), authRequest.getPassword());
        AuthResponse.Builder responseBuilder = (AuthResponse.Builder)
                this.appContext.getBean(AUTH_RESPONSE_BUILDER_BEAN);
        String clientIP = servletRequest.getAttribute(CLIENT_IP_ATTR).toString();

        if (userOpt.isEmpty()) {
            throw new AuthException.Builder(USER_NOT_AUTHORIZED, this.getClass())
                    .setIpAddress(clientIP)
                    .setMessage(String.format("Username '%s' is not authorized",
                            authRequest.getUsername()))
                    .build();
        }

        User user = userOpt.get();
        if (!userService.isUserDeviceAuthorized(user, authRequest.getDevice_id())) {
            throw new AuthException.Builder(DEVICE_NOT_AUTHORIZED, this.getClass())
                    .setIpAddress(clientIP)
                    .setMessage(String.format("Device with ID '%s' is not authorized",
                            authRequest.getDevice_id()))
                    .build();
        }

        return responseBuilder.buildSuccessResponse(authRequest.getDevice_id(), user, servletResponse);
    }
}
