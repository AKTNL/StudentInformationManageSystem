package com.student.studentmanagesystembackend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Course {
    private Long courseId;
    private String courseName;
    private String description;
    private String coverImg;
    private String resourceUrl; // 存 MinIO 返回的地址
    private String resourceType; // video 或 pdf

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Integer weekDay;
    private Integer section;
    private String location;
}
