package com.ecc.exceptions;

import lombok.Getter;

@Getter
public enum ExceptionCollection {
    SUCCESS(200, "success"),

    CONTRACT_VERIFY_FAILED_NO_SUCH_TYPE_SUPPORT(7001, "Invalid type of verification!"),
    CONTRACT_VERIFY_FAILED(7000, "Contract verify failed!"),

    USER_EMAIL_ALREADY_REGISTERED(3001, "Email already been registered!"),
    USER_EMAIL_NOT_REGISTERED(3002, "Email not registered!"),
    USER_VERIFICATION_ERROR(4001, "Verify failed!"),
    USER_NOT_EXISTS(3003, "User not exists!"),

    BLOCK_INSERT_ERROR(7500, "Contract verify error! Can't add to local block!"),

    KEY_PRIVATE_KEY_NOT_EXISTS(5001, "Private key file not exists!"),
    KEY_PUBLIC_KEY_NOT_EXISTS(5002, "Public key file not exists!"),
    KEY_KEY_FILES_NOT_EXISTS(5003, "Key files not exists!"),

    FILE_UPLOAD_FAILED(6001, "File upload failed!"),
    FILE_DOWNLOAD_FAILED(6002, "File download failed!"),
    FILE_TICKET_CREATE_ERROR(6003, "File ticket create error!File not belongs to you!"),
    FILE_TICKET_UNRECOGNIZED_ERROR(6004, "File download ticket unrecognized!"),
    FILE_TICKET_USAGE_ERROR(6005, "Ticket usage denied! Not signed for you to view the file!"),
    FILE_TICKET_REVOKED_ERROR(6006, "Ticket was revoked!"),
    FILE_PUSH_ERROR(6007, "File damaged!"),
    FILE_DOWNLOAD_COMBINE_ERROR(6008, "Sorry, file cannot be combined or recovered!"),
    FILE_SPLIT_PLATFORM_NOT_SUPPORT(6009,"Split error, your system platform not supported!"),
    FILE_SPLIT_ERROR(6010,"Cannot split file!"),
    FILE_COMBINE_ERROR(6011,"Cannot combine file!")
    ;


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
