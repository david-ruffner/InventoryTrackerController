package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.config.EndpointConfig;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.AllowedIPAddressService;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.*;
import static com.davidruffner.inventorytrackercontroller.util.Constants.CLIENT_IP_ATTR;
import static com.davidruffner.inventorytrackercontroller.util.Constants.USER_ATTR;
import static com.davidruffner.inventorytrackercontroller.util.Utils.getClientIpAddress;

@Component
public class PreRequestHandler implements HandlerInterceptor {
    @Autowired
    AllowedIPAddressService addressService;

    @Autowired
    UserService userService;

    @Autowired
    EndpointConfig endpointConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws ControllerException {
        String clientIp = getClientIpAddress(request);
        request.setAttribute(CLIENT_IP_ATTR, clientIp);

        if (!addressService.isIPAddressAllowed(clientIp)) {
            throw new ControllerException.Builder(NOT_A_CHANCE, this.getClass())
                    .withErrorMessage(String.format("IP Address: %s is not allowed " +
                            "to access this resource", clientIp))
                    .withUnauthorizedResponseMessage()
                    .withIpAddress(request)
                    .build();
        }

        // Only check for the token if the request isn't trying to get the token in the first place
        String requestURI = request.getRequestURI();
        if (!requestURI.equals("/auth/token")) {
            String tokenHeader = request.getHeader("Authorization");
            if (null == tokenHeader || tokenHeader.isEmpty()) {
                throw new ControllerException.Builder(BAD_REQUEST, this.getClass())
                        .withResponseMessage("Request must include bearer token " +
                                "in Authorization header")
                        .withIpAddress(request)
                        .build();
            }
            String token = tokenHeader.replace("Bearer", "").trim();
            User user = userService.getUserFromToken(token, request);

            // Check if user is trying to access an admin endpoint
            if (endpointConfig.isEndpointAdmin(requestURI) && !user.isAdmin()) {
                throw new ControllerException.Builder(USER_NOT_AUTHORIZED, this.getClass())
                        .withErrorMessage(String.format("User '%s' is not allowed to " +
                                "access this endpoint", user.getUserId()))
                        .withUnauthorizedResponseMessage()
                        .withIpAddress(request)
                        .build();
            }

            request.setAttribute(USER_ATTR, user);
        }

        return true;
    }
}
