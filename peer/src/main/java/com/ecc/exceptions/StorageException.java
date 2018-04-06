package com.ecc.exceptions;

public class StorageException extends RuntimeException {
    public StorageException() {
        super();
    }

    public StorageException(String message) {
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
