package com.student.studentmanagesystembackend.constant;

public class UserConstants {
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_STUDENT = 2;
    public static final int ROLE_TEACHER = 3;

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_LOCKED = 0;

    public static final int MAX_LOGIN_FAIL_COUNT = 5;

    public static final String DEFAULT_NICKNAME = "用户";

    private UserConstants() {
    }
}
