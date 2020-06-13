package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {

    public ForbiddenException(String errorCode, String message) {
        super(HttpStatus.FORBIDDEN.value(), errorCode, message);
    }

    public ForbiddenException(String errorCode, String message, Throwable throwable) {
        super(HttpStatus.FORBIDDEN.value(), errorCode, message, throwable);
    }
}
