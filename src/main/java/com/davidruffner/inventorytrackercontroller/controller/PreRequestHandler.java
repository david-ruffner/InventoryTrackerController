package com.davidruffner.inventorytrackercontroller.controller;

import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.db.services.AllowedIPAddressService;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import com.davidruffner.inventorytrackercontroller.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.NOT_A_CHANCE;
import static com.davidruffner.inventorytrackercontroller.util.Constants.CLIENT_IP_ATTR;
import static com.davidruffner.inventorytrackercontroller.util.Utils.getClientIpAddress;

@Component
public class PreRequestHandler implements HandlerInterceptor {
    @Autowired
    AllowedIPAddressService addressService;

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
        } else {
            return true;
        }
    }
}
