package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.entity.Course;
import com.student.studentmanagesystembackend.mapper.CourseMapper;
import com.student.studentmanagesystembackend.websocket.WebSocketServer;
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

        // 发布成功后，推送消息给所有在线用户
        // 消息格式可以是 JSON，这里简单发个字符串
        WebSocketServer.sendInfo("新课上架：" + course.getCourseName());

        return Result.success("发布成功");
    }

    @DeleteMapping("courses/{id}")
    public Result<String> delete(@PathVariable Long id){
        courseMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
