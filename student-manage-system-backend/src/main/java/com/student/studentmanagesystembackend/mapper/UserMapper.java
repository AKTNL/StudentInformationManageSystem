package com.student.studentmanagesystembackend.mapper;

import com.student.studentmanagesystembackend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    // 根据用户名查用户（用于检查是否重复，以及登录）
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    // 插入新用户
    @Insert("INSERT INTO users(username, password, salt, nickname, role, status, create_time) " +
            "VALUES(#{username}, #{password}, #{salt}, #{nickname}, #{role}, 1, NOW())")
    void insert(User user);

    //更新登录失败次数和状态
    @org.apache.ibatis.annotations.Update("update users set login_fail_count = #{count}, status = #{status} where user_id = #{userId}")
    void updateLoginFail(Long userId, Integer count, Integer status);

    //重置失败次数（登录成功时调用）
    @org.apache.ibatis.annotations.Update("update users set login_fail_count = 0 where user_id = #{userId}")
    void resetLoginFail(Long userId);
}
