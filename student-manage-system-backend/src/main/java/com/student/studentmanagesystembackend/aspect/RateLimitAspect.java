package com.student.studentmanagesystembackend.aspect;

import com.student.studentmanagesystembackend.annotation.RateLimit;
import com.student.studentmanagesystembackend.common.BusinessException;
import com.student.studentmanagesystembackend.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("@annotation(com.student.studentmanagesystembackend.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if (rateLimit == null) {
            return point.proceed();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = getClientIp(request);
        String key = "rate_limit:" + method.getName() + ":" + ip;

        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, rateLimit.time(), TimeUnit.SECONDS);
        }

        if (count > rateLimit.value()) {
            log.warn("IP {} 访问接口 {} 频率超限", ip, method.getName());
            throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED, rateLimit.message());
        }

        return point.proceed();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
