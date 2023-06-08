package com.hw.cloudstorage.exceptions;

import lombok.Getter;

@Getter
public class Error {
    private final String message;
    private final int id;
    public Error(String message, int id) {
        this.message = message;
        this.id = id;
    }

}