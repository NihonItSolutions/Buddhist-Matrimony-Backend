package com.matrimony.backend.exception;

import org.springframework.http.HttpStatus;

public class PaymentVerificationException extends ApiException {
    public PaymentVerificationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
