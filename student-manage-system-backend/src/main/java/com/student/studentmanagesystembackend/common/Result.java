package com.student.studentmanagesystembackend.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = ErrorCode.SUCCESS.getCode();
        result.msg = ErrorCode.SUCCESS.getMessage();
        result.data = data;
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.code = ErrorCode.SUCCESS.getCode();
        result.msg = message;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = ErrorCode.INTERNAL_ERROR.getCode();
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.code = code;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        Result<T> result = new Result<>();
        result.code = errorCode.getCode();
        result.msg = errorCode.getMessage();
        return result;
    }
}
