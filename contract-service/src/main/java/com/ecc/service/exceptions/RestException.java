package com.ecc.service.exceptions;

public class RestException extends RuntimeException {
    public RestException() {
        super();
    }

    public RestException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
