package com.student.studentmanagesystembackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_PERMISSION_KEY = "user:permission:";
    private static final String USER_INFO_KEY = "user:info:";
    private static final long DEFAULT_EXPIRE_TIME = 2;

    public void setUserPermissions(Long userId, Object permissions) {
        String key = USER_PERMISSION_KEY + userId;
        redisTemplate.opsForValue().set(key, permissions, DEFAULT_EXPIRE_TIME, TimeUnit.HOURS);
    }

    public Object getUserPermissions(Long userId) {
        String key = USER_PERMISSION_KEY + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteUserPermissions(Long userId) {
        String key = USER_PERMISSION_KEY + userId;
        redisTemplate.delete(key);
    }

    public void setUserInfo(Long userId, Object userInfo) {
        String key = USER_INFO_KEY + userId;
        redisTemplate.opsForValue().set(key, userInfo, DEFAULT_EXPIRE_TIME, TimeUnit.HOURS);
    }

    public Object getUserInfo(Long userId) {
        String key = USER_INFO_KEY + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteUserInfo(Long userId) {
        String key = USER_INFO_KEY + userId;
        redisTemplate.delete(key);
    }

    public void clearUserCache(Long userId) {
        deleteUserPermissions(userId);
        deleteUserInfo(userId);
    }
}
