package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.entity.User;
import com.student.studentmanagesystembackend.mapper.UserMapper;
import com.student.studentmanagesystembackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


}
