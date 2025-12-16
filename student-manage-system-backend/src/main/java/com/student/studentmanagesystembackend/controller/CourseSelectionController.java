package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.entity.Course;
import com.student.studentmanagesystembackend.entity.Student;
import com.student.studentmanagesystembackend.mapper.StudentCourseMapper;
import com.student.studentmanagesystembackend.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-select")
public class CourseSelectionController {
    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Autowired
    private StudentMapper studentMapper;

    //1.选课接口
    @PostMapping("/select")
    public Result<String> selectCourse(@RequestParam Long userId, @RequestParam Long courseId){
        Student student = studentMapper.findByUserId(userId);
        if (student == null) return Result.error("学生档案不存在");

        //检查是否重复选课
        int count = studentCourseMapper.countByStudentAndCourse(student.getId(), courseId);
        if (count > 0){
            return Result.error("你已经选过这门课了");
        }

        studentCourseMapper.insert(student.getId(), courseId);
        return Result.success("选课成功");
    }

    //2.退课接口
    @PostMapping("/drop")
    public Result<String> dropCourse(@RequestParam Long userId, @RequestParam Long courseId){
        Student student = studentMapper.findByUserId(userId);
        if (student == null) return Result.error("学生档案不存在");

        studentCourseMapper.delete(student.getId(), courseId);
        return Result.success("退课成功");
    }

    //3.获取我的课表
    @GetMapping("/my-courses")
    public Result<List<Course>> getMyCourses(@RequestParam Long userId){
        Student student = studentMapper.findByUserId(userId);
        if (student == null) return Result.error("学生档案不存在");

        List<Course> list = studentCourseMapper.findMyCourses(student.getId());
        return Result.success(list);
    }
}
