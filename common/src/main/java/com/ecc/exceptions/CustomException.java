package com.ecc.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomException extends RuntimeException {
    private int code;
    private String message;
    private ExceptionCollection exceptionCollection;

    public CustomException(ExceptionCollection exceptionCollection) {
        super(exceptionCollection.getMessage());
        this.exceptionCollection = exceptionCollection;
    }

    public CustomException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
