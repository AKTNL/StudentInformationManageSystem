package com.student.studentmanagesystembackend.service.impl;

import com.student.studentmanagesystembackend.common.SecureUtils;
import com.student.studentmanagesystembackend.entity.User;
import com.student.studentmanagesystembackend.mapper.UserMapper;
import com.student.studentmanagesystembackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void register(User user) {
        //1.校验用户名是否已存在
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        //2.生成盐值
        String salt = SecureUtils.getSalt();

        //3.密码加密（明文 + 盐 -> 密文）
        String encodedPwd = SecureUtils.md5(user.getPassword(), salt);

        //4. 设置回对象
        user.setSalt(salt);
        user.setPassword(encodedPwd);

        //默认角色为学生
        if(user.getRole() == null) user.setRole(2);
        //默认昵称
        if(user.getNickname() == null) user.setNickname("同学_" + salt);

        //5.存入数据库
        userMapper.insert(user);
    }

    @Override
    public User login(String username, String password) {
        //1.根据用户名查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        //2.检查锁定状态
        if (user.getStatus() != null && user.getStatus() == 0){
            throw new RuntimeException("账号已被锁定，请联系管理员");
        }

        //3.对比密码
        //数据库里的盐
        String salt = user.getSalt();
        //用同样的算法加密用户输入的密码
        String inputPwdEncoded = SecureUtils.md5(password, salt);

        //4.判断结果
        if(!user.getPassword().equals(inputPwdEncoded)){
            //密码错误逻辑
            int newFailCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
            int newStatus = 1; //默认正常

            //如果错误次数 >= 5,锁定账号
            if(newFailCount >= 5){
                newStatus = 0; //锁定
            }

            //更新数据库
            userMapper.updateLoginFail(user.getUserId(), newFailCount, newStatus);

            String msg = "密码错误，您还有" + (5 - newFailCount) + "次机会";
            if(newStatus == 0){
                msg = "密码错误，账号已锁定，请联系管理员";
            }
            throw new RuntimeException(msg);
        }

        //密码正确的逻辑
        //重置错误次数
        userMapper.resetLoginFail(user.getUserId());

        //敏感信息不返回给前端
        user.setPassword(null);
        user.setSalt(null);

        return user;
    }
}
