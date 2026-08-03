package com.matrimony.backend.exception;

import org.springframework.http.HttpStatus;

public class ProfileIncompleteException extends ApiException {
    public ProfileIncompleteException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
