package com.student.studentmanagesystembackend.aspect;

import com.student.studentmanagesystembackend.annotation.AuthCheck;
import com.student.studentmanagesystembackend.common.BusinessException;
import com.student.studentmanagesystembackend.common.ErrorCode;
import com.student.studentmanagesystembackend.context.UserContext;
import com.student.studentmanagesystembackend.entity.User;
import com.student.studentmanagesystembackend.mapper.UserMapper;
import com.student.studentmanagesystembackend.service.CacheService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Component
public class AuthAspect {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CacheService cacheService;

    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustPermission = authCheck.value();
        int[] requiredRoles = authCheck.roles();
        boolean requireLogin = authCheck.requireLogin();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userIdStr = request.getHeader("userId");

        if (requireLogin && userIdStr == null) {
            log.warn("未登录访问受保护接口: {}", request.getRequestURI());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        if (!requireLogin && userIdStr == null) {
            return joinPoint.proceed();
        }

        Long userId = Long.parseLong(userIdStr);
        
        User user = (User) cacheService.getUserInfo(userId);
        if (user == null) {
            user = userMapper.selectById(userId);
            if (user != null) {
                cacheService.setUserInfo(userId, user);
            }
        }

        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("账号已被锁定: userId={}", userId);
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        UserContext.set(user);

        if (requiredRoles.length > 0) {
            int userRole = user.getRole() != null ? user.getRole() : 0;
            boolean hasRole = false;
            for (int role : requiredRoles) {
                if (role == userRole) {
                    hasRole = true;
                    break;
                }
            }
            if (!hasRole) {
                log.warn("用户角色不足: userId={}, userRole={}, requiredRoles={}", 
                        userId, user.getRole(), Arrays.toString(requiredRoles));
                throw new BusinessException(ErrorCode.FORBIDDEN, "权限不足");
            }
        }

        if (mustPermission != null && !mustPermission.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<String> userPermissions = (List<String>) cacheService.getUserPermissions(userId);
            if (userPermissions == null) {
                userPermissions = userMapper.findPermissionsByUserId(userId);
                if (userPermissions != null) {
                    cacheService.setUserPermissions(userId, userPermissions);
                }
            }
            
            if (userPermissions == null || !userPermissions.contains(mustPermission)) {
                log.warn("用户权限不足: userId={}, requiredPermission={}", userId, mustPermission);
                throw new BusinessException(ErrorCode.FORBIDDEN, "权限不足: " + mustPermission);
            }
        }

        try {
            return joinPoint.proceed();
        } finally {
            UserContext.remove();
        }
    }
}
