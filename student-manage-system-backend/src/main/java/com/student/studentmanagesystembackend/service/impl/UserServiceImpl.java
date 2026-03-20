package com.student.studentmanagesystembackend.service.impl;

import com.student.studentmanagesystembackend.common.BusinessException;
import com.student.studentmanagesystembackend.common.ErrorCode;
import com.student.studentmanagesystembackend.common.SecureUtils;
import com.student.studentmanagesystembackend.constant.StudentConstants;
import com.student.studentmanagesystembackend.constant.UserConstants;
import com.student.studentmanagesystembackend.entity.Student;
import com.student.studentmanagesystembackend.entity.User;
import com.student.studentmanagesystembackend.mapper.StudentMapper;
import com.student.studentmanagesystembackend.mapper.UserMapper;
import com.student.studentmanagesystembackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public void register(User user, String studentNo, String realName) {
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            log.warn("注册失败，用户名已存在: {}", user.getUsername());
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String salt = SecureUtils.getSalt();
        String encodedPwd = SecureUtils.md5(user.getPassword(), salt);

        user.setSalt(salt);
        user.setPassword(encodedPwd);

        if (user.getRole() == null) {
            user.setRole(UserConstants.ROLE_STUDENT);
        }
        if (user.getNickname() == null) {
            user.setNickname(realName);
        }

        userMapper.insert(user);
        log.info("用户注册成功: userId={}, username={}", user.getUserId(), user.getUsername());

        Student student = new Student();
        student.setUserId(user.getUserId());
        student.setStudentNo(studentNo);
        student.setRealName(realName);
        student.setGender(StudentConstants.GENDER_MALE);
        student.setPhone(StudentConstants.DEFAULT_PHONE);

        studentMapper.insert(student);
        log.info("学生信息创建成功: studentNo={}", studentNo);
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.warn("登录失败，用户不存在: {}", username);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (user.getStatus() != null && user.getStatus() == UserConstants.STATUS_LOCKED) {
            log.warn("登录失败，账号已被锁定: {}", username);
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        String salt = user.getSalt();
        String inputPwdEncoded = SecureUtils.md5(password, salt);

        if (!user.getPassword().equals(inputPwdEncoded)) {
            int newFailCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
            int newStatus = UserConstants.STATUS_NORMAL;

            if (newFailCount >= UserConstants.MAX_LOGIN_FAIL_COUNT) {
                newStatus = UserConstants.STATUS_LOCKED;
            }

            userMapper.updateLoginFail(user.getUserId(), newFailCount, newStatus);

            String msg = "密码错误，您还有" + (UserConstants.MAX_LOGIN_FAIL_COUNT - newFailCount) + "次机会";
            if (newStatus == UserConstants.STATUS_LOCKED) {
                log.warn("账号已锁定: {}", username);
                msg = "密码错误，账号已锁定，请联系管理员";
            }
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, msg);
        }

        userMapper.resetLoginFail(user.getUserId());
        log.info("用户登录成功: userId={}, username={}", user.getUserId(), username);

        user.setPassword(null);
        user.setSalt(null);

        return user;
    }

    @Override
    public String sendResetCode(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.warn("重置密码失败，用户不存在: {}", username);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        redisTemplate.opsForValue().set(
            "reset_code:" + username,
            code,
            5,
            TimeUnit.MINUTES
        );

        log.info("生成密码重置验证码: username={}, code={}", username, code);
        return code;
    }

    @Override
    public void resetPassword(String username, String code, String newPassword) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.warn("重置密码失败，用户不存在: {}", username);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        String key = "reset_code:" + username;
        String realCode = redisTemplate.opsForValue().get(key);

        if (realCode == null) {
            log.warn("重置密码失败，验证码已过期: username={}", username);
            throw new BusinessException(ErrorCode.CAPTCHA_EXPIRED, "验证码已过期，请重新获取");
        }

        if (!realCode.equals(code)) {
            log.warn("重置密码失败，验证码错误: username={}, input={}, real={}", username, code, realCode);
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "验证码错误");
        }

        redisTemplate.delete(key);

        String salt = SecureUtils.getSalt();
        String encodedPwd = SecureUtils.md5(newPassword, salt);

        userMapper.updatePassword(user.getUserId(), encodedPwd, salt);

        log.info("密码重置成功: userId={}, username={}", user.getUserId(), username);
    }
}
