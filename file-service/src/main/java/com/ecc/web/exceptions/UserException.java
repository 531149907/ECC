package com.ecc.web.exceptions;

import lombok.Getter;

@Getter
public class UserException extends Exception {
    private int code;

    public UserException(String message, int code) {
        super(message);
        this.code = code;
    }
}
