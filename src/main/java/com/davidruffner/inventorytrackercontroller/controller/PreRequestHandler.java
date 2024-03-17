package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.config.EndpointConfig;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.services.AllowedIPAddressService;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.NOT_A_CHANCE;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.USER_NOT_AUTHORIZED;
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
                             Object handler) throws Exception {
        String clientIp = getClientIpAddress(request);
        request.setAttribute(CLIENT_IP_ATTR, clientIp);

        if (!addressService.isIPAddressAllowed(clientIp)) {
            throw new AuthException.Builder(NOT_A_CHANCE, this.getClass())
                    .setMessage(String.format("IP Address: %s is not allowed " +
                            "to access this resource", clientIp))
                    .setIpAddress(clientIp)
                    .build();
        }

        // Only check for the token if the request isn't trying to get the token in the first place
        String requestURI = request.getRequestURI();
        if (!requestURI.equals("/auth/token")) {
            String tokenHeader = request.getHeader("Authorization");
            if (null == tokenHeader || tokenHeader.isEmpty()) {
                throw new BadRequestException.Builder(this.getClass(),
                        "Client must include bearer token in Authorization header").build();
            }
            String token = tokenHeader.replace("Bearer", "").trim();
            User user = userService.getUserFromToken(token, request);

            // Check if user is trying to access an admin endpoint
            if (endpointConfig.isEndpointAdmin(requestURI) && !user.isAdmin()) {
                throw new AuthException.Builder(USER_NOT_AUTHORIZED, this.getClass())
                        .setMessage("User is not allowed to access this endpoint")
                        .setIpAddress(clientIp)
                        .build();
            }

            request.setAttribute(USER_ATTR, user);
        }

        return true;
    }
}
