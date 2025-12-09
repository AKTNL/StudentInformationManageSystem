package com.student.studentmanagesystembackend.service.impl;

import com.student.studentmanagesystembackend.entity.Student;
import com.student.studentmanagesystembackend.mapper.StudentMapper;
import com.student.studentmanagesystembackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

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
        studentMapper.deleteById(id);
    }

    @Override
    public void updateStudent(Student student) {
        studentMapper.update(student);
    }
}
