package com.student.studentmanagesystembackend.mapper;

import com.student.studentmanagesystembackend.entity.Course;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentCourseMapper {
    // 1.选课
    @Insert("insert into student_course(student_id, course_id, create_time) values(#{studentId}, #{courseId}, NOW())")
    void insert(Long studentId, Long courseId);

    // 2.退课
    @Delete("delete from student_course where student_id = #{studentId} and course_id = #{courseId}")
    void delete(Long studentId, Long courseId);

    // 3.检查是否已选
    @Select("select count(*) from student_course where student_id = #{studentId} and course_id = #{courseId}")
    int countByStudentAndCourse(Long studentId, Long courseId);

    // 4.查询某个学生已选课程
    @Select("select c.* from courses c " +
            "inner join student_course sc on c.course_id = sc.course_id " +
            "where sc.student_id = #{studentId}")
    List<Course> findMyCourses(Long studentId);
}
