package com.matrimony.backend.exception;

import org.springframework.http.HttpStatus;

public class DailyLimitExceededException extends ApiException {
    public DailyLimitExceededException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, message);
    }
}
