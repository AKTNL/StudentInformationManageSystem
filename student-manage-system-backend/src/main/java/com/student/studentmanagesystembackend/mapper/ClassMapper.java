package com.student.studentmanagesystembackend.mapper;

import com.student.studentmanagesystembackend.entity.SchoolClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClassMapper {
    @Select("select * from classes")
    List<SchoolClass> findAll();
}
