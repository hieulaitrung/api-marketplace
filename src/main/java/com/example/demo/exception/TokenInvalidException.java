package com.example.demo.exception;

import com.example.demo.constant.ErrorCode;
import org.springframework.http.HttpStatus;

public class TokenInvalidException extends DemoException {
    public TokenInvalidException(String message, Throwable throwable) {
        super(HttpStatus.UNAUTHORIZED.value(), ErrorCode.TOKEN_INVALID.toString(), message, throwable);
    }
}
