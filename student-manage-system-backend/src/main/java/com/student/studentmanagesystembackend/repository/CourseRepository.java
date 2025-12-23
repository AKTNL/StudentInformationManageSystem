package com.student.studentmanagesystembackend.repository;

import com.student.studentmanagesystembackend.entity.CourseDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDoc, Long> {

    // Spring Data 会自动根据方法名生成查询逻辑
    // 相当于: select * from course where courseName like %name% or description like %name%
    List<CourseDoc> findByCourseNameOrDescription(String name, String desc);
}
