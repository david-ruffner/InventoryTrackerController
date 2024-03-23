package com.davidruffner.inventorytrackercontroller.controller.requests;

import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.List;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.BAD_REQUEST;
import static com.davidruffner.inventorytrackercontroller.util.Utils.isListNullOrEmpty;

public class AddIPAddressRequest {
    private List<String> ipv4Addresses;
    private List<String> ipv6Addresses;

    public AddIPAddressRequest(List<String> ipv4Addresses, List<String> ipv6Addresses)
    throws ControllerException {
        this.ipv4Addresses = ipv4Addresses;
        this.ipv6Addresses = ipv6Addresses;

        if (isListNullOrEmpty(this.ipv4Addresses) && isListNullOrEmpty(this.ipv6Addresses)) {
            throw new ControllerException.Builder(BAD_REQUEST, this.getClass())
                    .withResponseMessage("At least one of 'ipv4Addresses' and 'ipv6Addresses'" +
                            " parameters are required")
                    .build();
        }

        InetAddressValidator addressValidator = new InetAddressValidator();
        if (null != this.ipv4Addresses) {
            for (String address : this.ipv4Addresses) {
                if (!addressValidator.isValidInet4Address(address)) {
                    throw new ControllerException.Builder(BAD_REQUEST, this.getClass())
                            .withResponseMessage(String.format("IPv4 Address '%s' is invalid", address))
                            .build();
                }
            }
        }

        if (null != this.ipv6Addresses) {
            for (String address : this.ipv6Addresses) {
                if (!addressValidator.isValidInet6Address(address)) {
                    throw new ControllerException.Builder(BAD_REQUEST, this.getClass())
                            .withResponseMessage(String.format("IPv6 Address '%s' is invalid", address))
                            .build();
                }
            }
        }
    }

    public List<String> getIpv4Addresses() {
        return ipv4Addresses;
    }

    public List<String> getIpv6Addresses() {
        return ipv6Addresses;
    }
}
