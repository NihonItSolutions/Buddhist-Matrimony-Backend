package com.matrimony.backend.exception;

import org.springframework.http.HttpStatus;

public class OtpVerificationException extends ApiException {
    public OtpVerificationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
