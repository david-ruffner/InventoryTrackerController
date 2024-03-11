package com.davidruffner.inventorytrackercontroller.controller.requests;

import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;
import com.davidruffner.inventorytrackercontroller.util.Utils;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.List;

import static com.davidruffner.inventorytrackercontroller.util.Utils.isListNullOrEmpty;

public class AddIPAddressRequest extends BaseRequest {
    private List<String> ipv4Addresses;
    private List<String> ipv6Addresses;

    public AddIPAddressRequest(List<String> ipv4Addresses, List<String> ipv6Addresses)
            throws BadRequestException {
        this.ipv4Addresses = ipv4Addresses;
        this.ipv6Addresses = ipv6Addresses;
        validate();
    }

    @Override
    protected void validate() throws BadRequestException {
        if (isListNullOrEmpty(this.ipv4Addresses) && isListNullOrEmpty(this.ipv6Addresses)) {
            throwBadRequest("At least one of 'ipv4Addresses' and 'ipv6Addresses'" +
                    " parameters are required");
        }

        InetAddressValidator addressValidator = new InetAddressValidator();
        if (null != this.ipv4Addresses) {
            for (String address : this.ipv4Addresses) {
                if (!addressValidator.isValidInet4Address(address)) {
                    throwBadRequest(String.format("IPv4 Address '%s' is invalid", address));
                }
            }
        }

        if (null != this.ipv6Addresses) {
            for (String address : this.ipv6Addresses) {
                if (!addressValidator.isValidInet6Address(address)) {
                    throwBadRequest(String.format("IPv6 Address '%s' is invalid", address));
                }
            }
        }
    }

    @Override
    protected void throwBadRequest(String errMsg) throws BadRequestException {
        throw new BadRequestException.Builder(this.getClass(), errMsg).build();
    }

    public List<String> getIpv4Addresses() {
        return ipv4Addresses;
    }

    public List<String> getIpv6Addresses() {
        return ipv6Addresses;
    }
}
