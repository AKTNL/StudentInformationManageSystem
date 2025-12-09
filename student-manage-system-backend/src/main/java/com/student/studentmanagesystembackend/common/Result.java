package com.student.studentmanagesystembackend.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; //状态码: 200成功，500失败
    private String msg; //提示信息
    private T data; //返回的数据

    //成功时调用的方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = "操作成功！";
        result.data = data;
        return result;
    }

    //成功但不需要返回数据
    public static <T> Result<T> success() {
        return success(null);
    }

    //失败时调用的方法
    public static <T> Result<T> error(String msg){
        Result<T> result = new Result<>();
        result.code = 500;
        result.msg = msg;
        return result;
    }
}
