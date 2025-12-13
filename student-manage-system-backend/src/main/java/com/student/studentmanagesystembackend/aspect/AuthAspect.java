package com.student.studentmanagesystembackend.aspect;

import com.student.studentmanagesystembackend.annotation.AuthCheck;
import com.student.studentmanagesystembackend.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Aspect // 标记为切面
@Component // 交给 Spring 管理
public class AuthAspect {
    @Autowired
    private UserMapper userMapper;

    @Around("@annotation(authCheck)") // 拦截所有带 @AuthCheck 注解的方法
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        //1.获取注解里的权限码
        String mustPermission = authCheck.value();
        if (mustPermission == null || mustPermission.equals("")){
            // 如果没写权限码，直接放行
            return joinPoint.proceed();
        }

        // 2. 获取当前请求的用户
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userIdStr = request.getHeader("userId");

        if(userIdStr == null){
            throw new RuntimeException("无权访问：未登录");
        }
        Long userId = Long.parseLong(userIdStr);

        // 3. 查用户 -> 查角色 -> 查权限
        // 这里为了性能，把用户的权限列表存到 Redis 里，不要每次都查库
        List<String> userPermissions = userMapper.findPermissionsByUserId(userId);

        // 4. 校验权限
        if (userPermissions == null || !userPermissions.contains(mustPermission)){
            throw new RuntimeException("无权访问：权限不足" + mustPermission);
        }

        // 5. 权限验证通过，执行原方法
        return joinPoint.proceed();
    }
}
