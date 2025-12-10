package com.student.studentmanagesystembackend.service.impl;

import com.student.studentmanagesystembackend.entity.Student;
import com.student.studentmanagesystembackend.mapper.StudentMapper;
import com.student.studentmanagesystembackend.mapper.UserMapper;
import com.student.studentmanagesystembackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void addStudent(Student student) {
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
