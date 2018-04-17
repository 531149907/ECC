package com.ecc.exceptions;

import lombok.Getter;

@Getter
public enum ExceptionCollection {
    USER_EMAIL_ALREADY_REGISTERED(3001, "Email already been registered!"),
    USER_EMAIL_NOT_REGISTERED(3002, "Email not registered!"),
    USER_VERIFICATION_ERROR(4001, "Verify failed!"),
    USER_NOT_EXISTS(3003,"User not exists!"),


    KEY_PRIVATE_KEY_NOT_EXISTS(5001, "Private key file not exists!"),
    KEY_PUBLIC_KEY_NOT_EXISTS(5002, "Public key file not exists!"),
    KEY_KEY_FILES_NOT_EXISTS(5003, "Key files not exists!"),


    FILE_UPLOAD_FAILED(6001, "File upload failed!"),
    FILE_DOWNLOAD_FAILED(6002, "File download failed!");


    private int code;
    private String message;

    ExceptionCollection(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ExceptionCollection{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
