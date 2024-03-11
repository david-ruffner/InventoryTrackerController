package com.davidruffner.inventorytrackercontroller.controller.requests;

import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;

public abstract class BaseRequest {
    protected abstract void validate() throws BadRequestException;

    protected abstract void throwBadRequest(String errMsg) throws BadRequestException;
}
