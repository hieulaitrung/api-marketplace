package com.example.demo.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemoException extends  Exception {

    protected Integer httpCode;

    protected String errorCode;

    public DemoException(Integer httpCode, String errorCode, String message) {
        super(message);
        this.httpCode = httpCode;
        this.errorCode =  errorCode;
    }

    public DemoException(Integer httpCode, String errorCode, String message, Throwable throwable) {
        super(message, throwable);
        this.httpCode = httpCode;
        this.errorCode =  errorCode;
    }
}
