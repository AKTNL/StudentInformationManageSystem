package com.student.studentmanagesystembackend.common;

public enum ErrorCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    ACCOUNT_LOCKED(1004, "账号已被锁定"),
    CAPTCHA_ERROR(1005, "验证码错误"),
    CAPTCHA_EXPIRED(1006, "验证码已过期"),
    LOGIN_FAIL_LIMIT(1007, "登录失败次数过多"),

    STUDENT_NOT_FOUND(2001, "学生信息不存在"),
    STUDENT_NO_EXISTS(2002, "学号已存在"),

    COURSE_NOT_FOUND(3001, "课程不存在"),
    COURSE_FULL(3002, "课程已满"),

    FILE_UPLOAD_ERROR(4001, "文件上传失败"),
    FILE_NOT_FOUND(4002, "文件不存在"),

    RATE_LIMIT_EXCEEDED(5001, "访问频率超限，请稍后再试");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
