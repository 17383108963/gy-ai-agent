package com.gy.gyaiagent.common;

import com.gy.gyaiagent.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author gyy
 * @version 1.1
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}

