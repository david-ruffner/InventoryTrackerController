package com.davidruffner.inventorytrackercontroller.db.entities;

import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.UUID;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.BAD_REQUEST;
import static com.davidruffner.inventorytrackercontroller.db.entities.AllowedIPAddress.IPVersion.IPV4;
import static com.davidruffner.inventorytrackercontroller.db.entities.AllowedIPAddress.IPVersion.IPV6;

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

    public enum IPVersion {
        IPV4,
        IPV6
    }

    public AllowedIPAddress(String ipAddress, IPVersion ipVersion) {
        this.ipAddressId = UUID.randomUUID().toString();
        if (ipVersion.equals(IPV4)) {
            this.ipv4Address = ipAddress;
        } else {
            this.ipv6Address = ipAddress;
        }
    }

    public IPVersion getIPVersion() {
        return (null == this.ipv6Address || this.ipv6Address.isEmpty()) ? IPV4 : IPV6;
    }

    /**
     * Checks if a given ipAddress string is a valid IPv4 or IPv6 address,
     * and then checks if that address is allowed to access the service.
     */
    public AllowedIPAddress(String ipAddress) throws ControllerException {
        InetAddressValidator validator = InetAddressValidator.getInstance();
        if (validator.isValidInet4Address(ipAddress)) {
            this.ipAddressId = UUID.randomUUID().toString();
            this.ipv4Address = ipAddress;
        } else if (validator.isValidInet6Address(ipAddress)) {
            this.ipAddressId = UUID.randomUUID().toString();
            this.ipv6Address = ipAddress;
        }
        else {
            throw new ControllerException.Builder(BAD_REQUEST, this.getClass())
                    .withResponseMessage(String.format("IP Address: %s is not " +
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
