package com.student.studentmanagesystembackend.service;

import com.student.studentmanagesystembackend.entity.User;

public interface UserService {
    void register(User user, String studentNo, String realName);

    User login(String username, String password);

    String sendResetCode(String username);

    void resetPassword(String username, String code, String newPassword);
}
