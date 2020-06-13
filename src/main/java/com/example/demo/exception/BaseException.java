package com.example.demo.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends  Exception {

    protected Integer httpCode;

    protected String errorCode;

    public BaseException(Integer httpCode, String errorCode, String message) {
        super(message);
        this.httpCode = httpCode;
        this.errorCode =  errorCode;
    }

    public BaseException(Integer httpCode, String errorCode, String message,Throwable throwable) {
        super(message, throwable);
        this.httpCode = httpCode;
        this.errorCode =  errorCode;
    }
}
