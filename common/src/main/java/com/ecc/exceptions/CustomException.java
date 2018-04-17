package com.ecc.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomException extends RuntimeException {
    private ExceptionCollection exceptionCollection;

    public CustomException(ExceptionCollection exceptionCollection) {
        super(exceptionCollection.getMessage());
        this.exceptionCollection = exceptionCollection;
    }
}
