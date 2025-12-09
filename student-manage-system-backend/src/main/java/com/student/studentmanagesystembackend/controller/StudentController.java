package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.entity.Student;
import com.student.studentmanagesystembackend.mapper.StudentMapper;
import com.student.studentmanagesystembackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentMapper studentMapper;

    @GetMapping("/students")
    public Result<List<Student>> list(){
        List<Student> list = studentService.getStudentList();
        return Result.success(list);
    }

    @PostMapping("/students")
    public Result<String> add(@RequestBody Student student){
        studentService.addStudent(student);
        return Result.success("添加成功");
    }

    @DeleteMapping("/students/{id}")
    public Result<String> delete(@PathVariable Long id){
        studentService.deleteStudent(id);
        return Result.success("删除成功");
    }

    @PutMapping("/students")
    public Result<String> update(@RequestBody Student student){
        studentService.updateStudent(student);
        return Result.success("修改成功");
    }

    //获取当前登录学生的个人档案
    @GetMapping("/student/my-info")
    public Result<Student> getMyInfo(@RequestParam Long userId) {
        Student student = studentMapper.findByUserId(userId);
        if (student == null) {
            return Result.error("未找到您的学生档案，请联系管理员绑定");
        }
        return Result.success(student);
    }
}
