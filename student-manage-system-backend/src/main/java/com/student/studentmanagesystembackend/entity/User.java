package com.student.studentmanagesystembackend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updateTime;
    
    private Long managedClassId;
}