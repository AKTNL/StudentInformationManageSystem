package com.student.studentmanagesystembackend.mapper;

import com.student.studentmanagesystembackend.entity.Course;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseMapper {
    @Select("select * from courses order by create_time DESC")
    List<Course> findAll();

    @Insert("insert into courses(course_name, description, cover_img, resource_url, resource_type, create_time) " +
            "values(#{courseName}, #{description}, #{coverImg}, #{resourceUrl}, #{resourceType}, NOW())")
    void insert(Course course);

    @Delete("delete from courses where course_id = #{courseId}")
    void deleteById(Long courseId);
}
