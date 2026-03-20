package com.student.studentmanagesystembackend.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    int value() default 100;
    int time() default 60;
    String message() default "访问频率超限，请稍后再试";
}
