package com.ecc.exceptions;

public class ContractException extends RuntimeException {
    public ContractException() {
        super();
    }

    public ContractException(String message) {
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
