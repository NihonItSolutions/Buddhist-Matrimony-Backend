package com.matrimony.backend.exception;

import org.springframework.http.HttpStatus;

public class SubscriptionRequiredException extends ApiException {
    public SubscriptionRequiredException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
