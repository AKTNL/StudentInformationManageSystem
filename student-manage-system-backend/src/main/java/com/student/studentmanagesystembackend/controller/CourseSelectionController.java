package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.annotation.AuthCheck;
import com.student.studentmanagesystembackend.common.BusinessException;
import com.student.studentmanagesystembackend.common.ErrorCode;
import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.constant.UserConstants;
import com.student.studentmanagesystembackend.context.UserContext;
import com.student.studentmanagesystembackend.entity.Course;
import com.student.studentmanagesystembackend.entity.Student;
import com.student.studentmanagesystembackend.entity.User;
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

    @PostMapping("/select")
    @AuthCheck(requireLogin = true)
    public Result<String> selectCourse(@RequestParam Long userId, @RequestParam Long courseId){
        User currentUser = UserContext.get();
        
        if(currentUser.getRole() == UserConstants.ROLE_STUDENT && !currentUser.getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能为自己选课");
        }

        Student student = studentMapper.findByUserId(userId);
        if (student == null) return Result.error("学生档案不存在");

        int count = studentCourseMapper.countByStudentAndCourse(student.getId(), courseId);
        if (count > 0){
            return Result.error("你已经选过这门课了");
        }

        studentCourseMapper.insert(student.getId(), courseId);
        return Result.success("选课成功");
    }

    @PostMapping("/drop")
    @AuthCheck(requireLogin = true)
    public Result<String> dropCourse(@RequestParam Long userId, @RequestParam Long courseId){
        User currentUser = UserContext.get();
        
        if(currentUser.getRole() == UserConstants.ROLE_STUDENT && !currentUser.getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能退选自己的课程");
        }

        Student student = studentMapper.findByUserId(userId);
        if (student == null) return Result.error("学生档案不存在");

        studentCourseMapper.delete(student.getId(), courseId);
        return Result.success("退课成功");
    }

    @GetMapping("/my-courses")
    @AuthCheck(requireLogin = true)
    public Result<List<Course>> getMyCourses(@RequestParam Long userId){
        User currentUser = UserContext.get();
        
        if(currentUser.getRole() == UserConstants.ROLE_STUDENT && !currentUser.getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能查看自己的课程");
        }

        Student student = studentMapper.findByUserId(userId);
        if (student == null) return Result.error("学生档案不存在");

        List<Course> list = studentCourseMapper.findMyCourses(student.getId());
        return Result.success(list);
    }
}
