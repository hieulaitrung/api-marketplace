package com.example.demo.controller.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiError {

    private String errorCode;

    private String message;


    public static ApiError valueOf(ErrorInfo error)  {
        ApiError r = new ApiError();
        r.errorCode = error.getErrorCode();
        r.message = error.getMessage();
        return r;
    }
}
