package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.entity.Course;
import com.student.studentmanagesystembackend.mapper.CourseMapper;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourseController {
    @Autowired
    private CourseMapper courseMapper;

    @GetMapping("/courses")
    public Result<List<Course>> list(){
        return Result.success(courseMapper.findAll());
    }

    @PostMapping("/courses")
    public Result<String> add(@RequestBody Course course){
        courseMapper.insert(course);
        return Result.success("发布成功");
    }

    @DeleteMapping("courses/{id}")
    public Result<String> delete(@PathVariable Long id){
        courseMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
