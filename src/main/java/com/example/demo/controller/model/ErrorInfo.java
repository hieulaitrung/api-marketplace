package com.example.demo.controller.model;

import lombok.Getter;
import lombok.Setter;


public enum ErrorInfo {

    PROTOCOL_UNAUTHORIZED("TOKEN_INVALID", "Token is invalid"),
    TOKEN_UNAUTHORIZED("TOKEN_UNAUTHORIZED", "Token is unauthorized to perform action"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token is expired");

    private ErrorInfo(String errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
    }

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private String errorCode;
    
}
