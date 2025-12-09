package com.student.studentmanagesystembackend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long userId;
    private String username;
    private String password;
    private String salt;
    private String nickname;
    private String avatar;
    private Integer role;
    private Integer status;
    private Integer loginFailCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}