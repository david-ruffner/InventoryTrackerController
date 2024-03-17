package com.davidruffner.inventorytrackercontroller.controller.requests;

import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;

public class AuthRequest extends BaseRequest {
    private String username;
    private String password;
    private String device_id;

    public AuthRequest(String username, String password, String device_id) {
        this.username = username;
        this.password = password;
        this.device_id = device_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDevice_id() {
        return device_id;
    }

    @Override
    protected void validate() throws BadRequestException {

    }

    @Override
    protected void throwBadRequest(String errMsg) throws BadRequestException {

    }
}
