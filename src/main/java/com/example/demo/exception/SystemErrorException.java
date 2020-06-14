package com.example.demo.exception;

import com.example.demo.constant.ErrorCode;
import org.springframework.http.HttpStatus;

public class SystemErrorException extends DemoException {

    public SystemErrorException(String message, Throwable throwable) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.SERVER_ERROR.toString(), message, throwable);
    }
}
