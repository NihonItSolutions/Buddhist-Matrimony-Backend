package com.matrimony.backend.exception;

import org.springframework.http.HttpStatus;

public class OtpExpiredException extends ApiException {
    public OtpExpiredException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
