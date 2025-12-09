package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.entity.SchoolClass;
import com.student.studentmanagesystembackend.mapper.ClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClassController {
    @Autowired
    private ClassMapper classMapper;

    @GetMapping("/classes")
    public Result<List<SchoolClass>> list(){
        return Result.success(classMapper.findAll());
    }
}
