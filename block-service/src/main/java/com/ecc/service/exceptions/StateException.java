package com.ecc.service.exceptions;

public class StateException extends RuntimeException {
    public StateException() {
        super();
    }

    public StateException(String message) {
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
