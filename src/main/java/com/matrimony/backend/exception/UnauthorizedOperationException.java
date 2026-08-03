package com.matrimony.backend.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedOperationException extends ApiException {
    public UnauthorizedOperationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
