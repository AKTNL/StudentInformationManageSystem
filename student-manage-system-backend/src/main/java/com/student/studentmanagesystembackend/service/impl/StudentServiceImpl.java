package com.student.studentmanagesystembackend.service.impl;

import com.student.studentmanagesystembackend.common.SecureUtils;
import com.student.studentmanagesystembackend.entity.Student;
import com.student.studentmanagesystembackend.entity.User;
import com.student.studentmanagesystembackend.mapper.StudentMapper;
import com.student.studentmanagesystembackend.mapper.UserMapper;
import com.student.studentmanagesystembackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Student> getStudentList() {
        return studentMapper.findAll();
    }

    @Override
    @Transactional
    public void addStudent(Student student) {
        // --- 1. 先自动帮学生创建一个登录账号 ---
        User user = new User();
        // 用户名 = 学号
        user.setUsername(student.getStudentNo());
        // 默认密码 = 123456
        String salt = SecureUtils.getSalt();
        String encodedPwd = SecureUtils.md5("123456", salt);
        user.setPassword(encodedPwd);
        user.setSalt(salt);

        user.setNickname(student.getRealName()); // 昵称 = 真实姓名
        user.setRole(2); // 角色 = 学生
        user.setStatus(1); // 状态 = 正常

        // 插入用户表
        User existUser = userMapper.findByUsername(user.getUsername());
        if(existUser != null) {
            throw new RuntimeException("该学号对应的账号已存在，无法重复添加！");
        }
        userMapper.insert(user);

        // --- 2. 再创建学生档案 ---
        // 关联刚才生成的 userId
        student.setUserId(user.getUserId());

        // 插入学生表
        studentMapper.insert(student);
    }

    @Override
    public void deleteStudent(Long id) {
        // 1. 先查询这个学生的信息，为了拿到 userId
        Student student = studentMapper.findById(id);

        if (student == null) {
            throw new RuntimeException("该学生不存在"); // 防止重复删除报错
        }

        // 2. 删除学生档案 (Students表)
        studentMapper.deleteById(id);

        // 3. 删除对应的登录账号 (Users表)
        if (student.getUserId() != null) {
            userMapper.deleteById(student.getUserId());
        }
    }

    @Override
    public void updateStudent(Student student) {
        studentMapper.update(student);
    }
}
