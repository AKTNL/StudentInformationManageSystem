package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.annotation.AuthCheck;
import com.student.studentmanagesystembackend.common.BusinessException;
import com.student.studentmanagesystembackend.common.ErrorCode;
import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.constant.UserConstants;
import com.student.studentmanagesystembackend.context.UserContext;
import com.student.studentmanagesystembackend.entity.Student;
import com.student.studentmanagesystembackend.entity.User;
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
    @AuthCheck(requireLogin = true)
    public Result<List<Student>> list(){
        List<Student> list = studentService.getStudentList();
        return Result.success(list);
    }

    @AuthCheck("student:add")
    @PostMapping("/students")
    public Result<String> add(@RequestBody Student student){
        studentService.addStudent(student);
        return Result.success("添加成功");
    }

    @AuthCheck("student:delete")
    @DeleteMapping("/students/{id}")
    public Result<String> delete(@PathVariable Long id){
        studentService.deleteStudent(id);
        return Result.success("删除成功");
    }

    @PutMapping("/students")
    @AuthCheck(requireLogin = true)
    public Result<String> update(@RequestBody Student student){
        User currentUser = UserContext.get();
        
        if(currentUser.getRole() == UserConstants.ROLE_STUDENT){
            Student currentStudent = studentMapper.findByUserId(currentUser.getUserId());
            if(currentStudent == null || !currentStudent.getId().equals(student.getId())){
                throw new BusinessException(ErrorCode.FORBIDDEN, "只能修改自己的信息");
            }
        }

        studentService.updateStudent(student);
        return Result.success("修改成功");
    }

    @GetMapping("/student/my-info")
    @AuthCheck(requireLogin = true)
    public Result<Student> getMyInfo(@RequestParam Long userId) {
        User currentUser = UserContext.get();
        
        if(currentUser.getRole() == UserConstants.ROLE_STUDENT && !currentUser.getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能查看自己的信息");
        }

        Student student = studentMapper.findByUserId(userId);
        if (student == null) {
            return Result.error("未找到您的学生档案，请联系管理员绑定");
        }
        return Result.success(student);
    }
}
