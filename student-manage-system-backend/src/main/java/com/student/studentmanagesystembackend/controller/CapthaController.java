package com.student.studentmanagesystembackend.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.student.studentmanagesystembackend.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class CapthaController {
    @Autowired
    private StringRedisTemplate redisTemplate; // 操作Redis的工具

    @GetMapping("/captcha")
    public Result<Map<String,String>> getCaptcha(){
        // 1. 生成验证码图片 (宽120，高40，4个字符，10条干扰线)
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 10);

        // 2. 获取验证码里的文字
        String code = captcha.getCode();

        // 3. 生成一个唯一的 Key (UUID)
        String key = UUID.randomUUID().toString();

        // 4. 存入 Redis (Key=UUID, Value=验证码, 过期时间=60秒)
        redisTemplate.opsForValue().set(key, code, 60, TimeUnit.SECONDS);

        // 5. 把图片转成 Base64 字符串 (前端可以直接用 <img src="..."> 显示)
        String imageBase64 = captcha.getImageBase64Data();

        // 6. 返回给前端 Key 和 图片
        Map<String,String> map = new HashMap<>();
        map.put("key", key);
        map.put("image", imageBase64);

        return Result.success(map);
    }
}
