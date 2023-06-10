package com.hw.cloudstorage.exceptions;

public class UploadFileToFolderException extends RuntimeException {
    public UploadFileToFolderException(String errorMessage) {
        super(errorMessage);
    }
}
