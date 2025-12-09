package com.student.studentmanagesystembackend.service;

import com.student.studentmanagesystembackend.entity.User;

public interface UserService {
    void register(User user);

    User login(String username, String password);
}
