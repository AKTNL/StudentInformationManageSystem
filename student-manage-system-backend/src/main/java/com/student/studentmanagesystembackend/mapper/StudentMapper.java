package com.student.studentmanagesystembackend.mapper;

import com.student.studentmanagesystembackend.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentMapper {
    //联表查询：查学生表(s)和班级表(c)
    //把c.class_name赋值给Student对象的className字段
    @Select("select s.*, c.class_name as className from students s left join classes c on s.class_id = c.class_id")
    List<Student> findAll();

    //新增学生
    @org.apache.ibatis.annotations.Insert("insert into students(student_no,real_name,gender,class_id,phone,create_time,avatar) values (#{studentNo},#{realName},${gender},#{classId},#{phone},NOW(),#{avatar})")
    void insert(Student student);

    @org.apache.ibatis.annotations.Delete("delete from students where id = #{id}")
    void deleteById(Long id);

    //更新学生信息
    @org.apache.ibatis.annotations.Update("update students set student_no = #{studentNo}, real_name = #{realName}, gender = #{gender}, class_id = #{classId}, phone = #{phone}, avatar = #{avatar}, birthday = #{birthday}, email = #{email} where id =#{id}")
    void update(Student student);

    @Select("SELECT s.*, c.class_name as className, u.create_time as createTime " +
            "FROM students s " +
            "LEFT JOIN classes c ON s.class_id = c.class_id " +
            "LEFT JOIN users u ON s.user_id = u.user_id " +
            "WHERE s.user_id = #{userId}")
    Student findByUserId(Long userId);
}
