package com.hw.cloudstorage.model.enums;

public enum ErrorType {
    LOGIN_CREDENTIALS(101),
    LOGIN_EMPTY_LOGIN_PASSWORD(102),
    USER_NOT_FOUND(103),
    TOKEN_INVALID(104),
    TOKEN_EXPIRED(105),
    UNSUPPORTED_TOKEN(106),
    TOKEN_SIGN_INVALID(107),
    TOKEN_IS_NOT_JWT(108),
    JWT_AUTHENTICATION_INVALID(109),
    INVALID_ARGUMENTS_DATA(110),
    UPLOAD_FILE_SIZE_EXCEEDED(111),
    UPLOAD_FILE_TO_FOLDER_ERROR(112);

    private final int errorId;

    ErrorType(int errorId) {
        this.errorId = errorId;
    }

    public int getErrorId() {
        return errorId;
    }

}