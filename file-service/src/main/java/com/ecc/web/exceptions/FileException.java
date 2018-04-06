package com.ecc.web.exceptions;

import lombok.Getter;

@Getter
public class FileException extends Exception{
    private int code;

    public FileException(String message, int code) {
        super(message);
        this.code = code;
    }
}
