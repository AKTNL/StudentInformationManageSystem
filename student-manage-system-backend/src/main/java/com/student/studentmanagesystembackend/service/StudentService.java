package com.student.studentmanagesystembackend.service;

import com.student.studentmanagesystembackend.entity.Student;

import java.util.List;

public interface StudentService {
    List<Student> getStudentList();

    void addStudent(Student student);

    void deleteStudent(Long id);

    void updateStudent(Student student);
}
