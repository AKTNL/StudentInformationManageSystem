package com.student.studentmanagesystembackend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Student {
    private Long id;
    private Long userId;
    private String studentNo;
    private String realName;
    private Integer gender;
    private Long classId;

    // 格式化日期，只显示 年-月-日
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate birthday;
    private String phone;
    private String email;
    private String avatar;

    private String className;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
