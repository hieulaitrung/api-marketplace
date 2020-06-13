package com.example.demo.dto;

import com.example.demo.constant.ErrorCode;
import com.example.demo.exception.BaseException;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {

    private String errorCode;

    private String message;


    public static ErrorDTO valueOf(BaseException error)  {
        ErrorDTO r = new ErrorDTO();
        r.errorCode = error.getErrorCode();
        r.message = error.getMessage();
        return r;
    }

    public static ErrorDTO from(Exception error)  {
        ErrorDTO r = new ErrorDTO();
        r.errorCode = ErrorCode.SERVER_ERROR.toString();
        r.message = "Something went wrong";
        return r;
    }
}
