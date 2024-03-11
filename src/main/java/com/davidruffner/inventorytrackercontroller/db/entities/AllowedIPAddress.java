package com.davidruffner.inventorytrackercontroller.db.entities;

import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import jakarta.persistence.*;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.INVALID_IP_ADDRESS;

@Entity
@Table(name = "AllowedIPAddresses")
public class AllowedIPAddress {
    @Id
    @Column(name = "IP_Address_ID")
    private String ipAddressId;

    @Column(name = "IPv4_Address")
    private String ipv4Address;

    @Column(name = "IPv6_Address")
    private String ipv6Address;

    public AllowedIPAddress() {}

    public AllowedIPAddress(String ipAddress) throws AuthException {
        InetAddressValidator validator = InetAddressValidator.getInstance();
        if (validator.isValidInet4Address(ipAddress)) {
            this.ipAddressId = UUID.randomUUID().toString();
            this.ipv4Address = ipAddress;
        } else if (validator.isValidInet6Address(ipAddress)) {
            this.ipAddressId = UUID.randomUUID().toString();
            this.ipv6Address = ipAddress;
        }
        else {
            throw new AuthException.Builder(INVALID_IP_ADDRESS, this.getClass())
                    .setIpAddress(ipAddress)
                    .setMessage(String.format("IP Address: %s is not " +
                            "a valid IPv4 or IPv6 Address", ipAddress))
                    .build();
        }
    }

    public String getIpAddressId() {
        return ipAddressId;
    }

    public String getIpv4Address() {
        return ipv4Address;
    }

    public String getIpv6Address() {
        return ipv6Address;
    }
}
