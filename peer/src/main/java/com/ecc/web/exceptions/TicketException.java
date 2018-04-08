package com.ecc.web.exceptions;

import lombok.Getter;

@Getter
public class TicketException extends Exception {
    private int code;

    public TicketException(String message, int code) {
        super(message);
        this.code = code;
    }
}
