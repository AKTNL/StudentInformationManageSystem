package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.entity.User;
import com.student.studentmanagesystembackend.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 注册
    @Data
    static class RegisterRequest{
        private String username;
        private String password;
        private String confirmPassword;
        private String captchaKey;
        private String captchaCode;
    }
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest request){
        String key = request.getCaptchaKey();
        String code = request.getCaptchaCode();

        if (key == null || code == null) {
            return Result.error("请输入验证码");
        }

        String realCode = redisTemplate.opsForValue().get(key);
        if (realCode == null) {
            return Result.error("验证码已过期");
        }

        if (!realCode.equalsIgnoreCase(code)) {
            return Result.error("验证码错误");
        }

        // 验证通过，删除缓存
        redisTemplate.delete(key);

        try{
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setNickname("用户"+request.getUsername());
            userService.register(user);
            return Result.success("注册成功");
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @Data  // 内部类，用来接收登录参数
    static class LoginRequest{
        private String username;
        private String password;
        private String captchaKey;  // 前端传来的 UUID
        private String captchaCode; // 用户输入的验证码
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request){
        // --- 验证码校验 ---
        String key = request.getCaptchaKey();
        String code = request.getCaptchaCode();

        if (key == null || code == null) {
            return Result.error("请输入验证码");
        }

        // 去 Redis 查真正的验证码
        String realCode = redisTemplate.opsForValue().get(key);

        if (realCode == null) {
            return Result.error("验证码已过期，请刷新");
        }

        // 忽略大小写比对
        if (!realCode.equalsIgnoreCase(code)) {
            return Result.error("验证码错误");
        }

        // 验证通过后，把 Redis 里的删掉，防止重复使用
        redisTemplate.delete(key);

        try{
            User user = userService.login(request.getUsername(), request.getPassword());
            return Result.success(user);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }
}
