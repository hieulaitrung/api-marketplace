package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends DemoException {
    public ResourceNotFoundException(String errorCode, String message) {
        super(HttpStatus.NOT_FOUND.value(), errorCode, message);
    }
    public ResourceNotFoundException(String errorCode, String message, Throwable throwable) {
        super(HttpStatus.NOT_FOUND.value(), errorCode, message, throwable);
    }
}
