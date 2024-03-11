package com.davidruffner.inventorytrackercontroller.controller.requests;

import com.davidruffner.inventorytrackercontroller.annotations.OneOf;
import com.davidruffner.inventorytrackercontroller.annotations.RequiredParam;
import com.davidruffner.inventorytrackercontroller.annotations.Validation;
import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

public class AddIPAddressRequest extends BaseRequest {
    @RequiredParam
    private String token;
    @OneOf(group = "addresses")
    private List<String> ipv4Addresses;
    @OneOf(group = "addresses")
    private List<String> ipv6Addresses;

    public AddIPAddressRequest(String token,
                               List<String> ipv4Addresses,
                               List<String> ipv6Addresses) throws Exception {
        this.token = token;
        this.ipv4Addresses = ipv4Addresses;
        this.ipv6Addresses = ipv6Addresses;
        validate(this);
    }

    public String getToken() {
        return token;
    }

    public List<String> getIpv4Addresses() {
        return ipv4Addresses;
    }

    public List<String> getIpv6Addresses() {
        return ipv6Addresses;
    }
}
