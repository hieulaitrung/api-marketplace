package com.example.demo.exception;

import com.example.demo.constant.ErrorCode;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException(String message, Throwable throwable) {
        super(HttpStatus.UNAUTHORIZED.value(), ErrorCode.TOKEN_EXPIRED.toString(), message, throwable);
    }
}
