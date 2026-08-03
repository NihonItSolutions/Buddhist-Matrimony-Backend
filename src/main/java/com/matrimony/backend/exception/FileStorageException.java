package com.matrimony.backend.exception;

import org.springframework.http.HttpStatus;

public class FileStorageException extends ApiException {
    public FileStorageException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
