package com.ecc.web.exceptions;

import lombok.Getter;

@Getter
public class ContractException extends Exception {
    private int code;
    public ContractException(String message,int code) {
        super(message);
        this.code = code;
    }
}
