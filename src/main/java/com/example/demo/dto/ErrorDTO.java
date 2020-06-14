package com.example.demo.dto;

import com.example.demo.constant.ErrorCode;
import com.example.demo.exception.DemoException;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {

    private String errorCode;

    private String message;


    public static ErrorDTO valueOf(DemoException error)  {
        ErrorDTO r = new ErrorDTO();
        r.errorCode = error.getErrorCode();
        r.message = error.getMessage();
        return r;
    }

    public static ErrorDTO serverError() {
        ErrorDTO r = new ErrorDTO();
        r.errorCode = ErrorCode.SERVER_ERROR.toString();
        r.message = "Something went wrong";
        return r;
    }

    public static ErrorDTO forbidden() {
        ErrorDTO r = new ErrorDTO();
        r.errorCode = ErrorCode.TOKEN_UNAUTHORIZED.toString();
        r.message = "Token is unauthorized to perform action";
        return r;
    }

    public static ErrorDTO unauthenticated() {
        ErrorDTO r = new ErrorDTO();
        r.errorCode = ErrorCode.TOKEN_INVALID.toString();
        r.message = "Token is missing or invalid";
        return r;
    }

    public static ErrorDTO badRequest() {
        ErrorDTO r = new ErrorDTO();
        r.errorCode = ErrorCode.BAD_REQUEST.toString();
        r.message = "Invalid request";
        return r;
    }

    public static ErrorDTO methodNotAllowed() {
        ErrorDTO r = new ErrorDTO();
        r.errorCode = ErrorCode.METHOD_NOT_ALLOWED.toString();
        r.message = "Method is not allowed";
        return r;
    }

    public static ErrorDTO mediaTypeNotSupported() {
        ErrorDTO r = new ErrorDTO();
        r.errorCode = ErrorCode.UNSUPPORTED_MEDIA_TYPE.toString();
        r.message = "Media type is not supported";
        return r;
    }
}
