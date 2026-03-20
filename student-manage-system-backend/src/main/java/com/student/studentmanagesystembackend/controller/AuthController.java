package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.annotation.RateLimit;
import com.student.studentmanagesystembackend.common.BusinessException;
import com.student.studentmanagesystembackend.common.ErrorCode;
import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.entity.User;
import com.student.studentmanagesystembackend.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Data
    static class RegisterRequest {
        private String username;
        private String password;
        private String confirmPassword;
        private String captchaKey;
        private String captchaCode;
        private String studentNo;
        private String realName;
    }

    @PostMapping("/register")
    @RateLimit(value = 5, time = 60, message = "注册请求过于频繁，请稍后再试")
    public Result<String> register(@RequestBody RegisterRequest request) {
        String key = request.getCaptchaKey();
        String code = request.getCaptchaCode();

        if (key == null || code == null) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "请输入验证码");
        }

        String realCode = redisTemplate.opsForValue().get(key);
        if (realCode == null) {
            throw new BusinessException(ErrorCode.CAPTCHA_EXPIRED);
        }

        if (!realCode.equalsIgnoreCase(code)) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR);
        }

        redisTemplate.delete(key);

        if (request.getStudentNo() == null || request.getRealName() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "学号和姓名不能为空");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "密码长度不能少于6位");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "两次输入的密码不一致");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        userService.register(user, request.getStudentNo(), request.getRealName());

        return Result.success("注册成功");
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
        private String captchaKey;
        private String captchaCode;
    }

    @PostMapping("/login")
    @RateLimit(value = 10, time = 60, message = "登录请求过于频繁，请稍后再试")
    public Result<User> login(@RequestBody LoginRequest request) {
        String key = request.getCaptchaKey();
        String code = request.getCaptchaCode();

        if (key == null || code == null) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "请输入验证码");
        }

        String realCode = redisTemplate.opsForValue().get(key);

        if (realCode == null) {
            throw new BusinessException(ErrorCode.CAPTCHA_EXPIRED, "验证码已过期，请刷新");
        }

        if (!realCode.equalsIgnoreCase(code)) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR);
        }

        redisTemplate.delete(key);

        User user = userService.login(request.getUsername(), request.getPassword());
        return Result.success(user);
    }

    @Data
    static class SendCodeRequest {
        private String username;
        private String captchaKey;
        private String captchaCode;
    }

    @PostMapping("/send-reset-code")
    @RateLimit(value = 3, time = 60, message = "验证码发送过于频繁，请稍后再试")
    public Result<String> sendResetCode(@RequestBody SendCodeRequest request) {
        String key = request.getCaptchaKey();
        String code = request.getCaptchaCode();

        if (key == null || code == null) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "请输入图形验证码");
        }

        String realCode = redisTemplate.opsForValue().get(key);
        if (realCode == null) {
            throw new BusinessException(ErrorCode.CAPTCHA_EXPIRED, "图形验证码已过期");
        }

        if (!realCode.equalsIgnoreCase(code)) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "图形验证码错误");
        }

        redisTemplate.delete(key);

        String resetCode = userService.sendResetCode(request.getUsername());
        log.info("密码重置验证码已发送: username={}", request.getUsername());

        return Result.success("验证码已发送，请注意查收（演示模式：验证码为 " + resetCode + "）");
    }

    @Data
    static class ResetPasswordRequest {
        private String username;
        private String code;
        private String newPassword;
        private String confirmPassword;
    }

    @PostMapping("/reset-password")
    @RateLimit(value = 5, time = 60, message = "密码重置请求过于频繁，请稍后再试")
    public Result<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "密码长度不能少于6位");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "两次输入的密码不一致");
        }

        userService.resetPassword(request.getUsername(), request.getCode(), request.getNewPassword());

        return Result.success("密码重置成功，请使用新密码登录");
    }
}
