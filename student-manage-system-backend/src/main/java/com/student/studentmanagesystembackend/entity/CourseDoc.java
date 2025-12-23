package com.student.studentmanagesystembackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

// indexName = "course" 相当于数据库里的表名
@Data
@Document(indexName = "course")
public class CourseDoc {
    @Id
    private Long id; // 对应courseId

    // type = Text 表示这个字段要分词搜索
    @Field(type = FieldType.Text)
    private String courseName;

    @Field(type = FieldType.Text)
    private String description;

    // Keyword 表示不分词，精确匹配
    @Field(type = FieldType.Keyword)
    private String resourceType;

    @Field(type = FieldType.Keyword)
    private String coverImg;
}
