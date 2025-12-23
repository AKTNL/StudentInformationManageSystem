package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.entity.Course;
import com.student.studentmanagesystembackend.entity.CourseDoc;
import com.student.studentmanagesystembackend.mapper.CourseMapper;
import com.student.studentmanagesystembackend.repository.CourseRepository;
import com.student.studentmanagesystembackend.websocket.WebSocketServer;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourseController {
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseRepository courseRepository; // 注入 ES 操作类

    @GetMapping("/courses")
    public Result<List<Course>> list(){
        return Result.success(courseMapper.findAll());
    }

    @PostMapping("/courses")
    public Result<String> add(@RequestBody Course course){
        //1.存入MySQL
        courseMapper.insert(course);

        //2.存入ES
        CourseDoc doc = new CourseDoc();
        doc.setId(course.getCourseId());
        doc.setCourseName(course.getCourseName());
        doc.setDescription(course.getDescription());
        doc.setCoverImg(course.getCoverImg());

        courseRepository.save(doc); // save = 插入或更新

        // 发布成功后，推送消息给所有在线用户
        // 消息格式可以是 JSON，这里简单发个字符串
        WebSocketServer.sendInfo("新课上架：" + course.getCourseName());

        return Result.success("发布成功");
    }

    @DeleteMapping("courses/{id}")
    public Result<String> delete(@PathVariable Long id){
        courseMapper.deleteById(id);
        return Result.success("删除成功");
    }

    //搜索
    @GetMapping("/courses/search")
    public Result<List<CourseDoc>> search(@RequestParam String keyword){
        // 去 ES 里搜
        List<CourseDoc> list = courseRepository.findByCourseNameOrDescription(keyword, keyword);
        return Result.success(list);
    }

    @GetMapping("/courses/sync")
    public Result<String> syncData(){
        // 1. 从 MySQL 查出所有课程
        List<Course> mysqlList = courseMapper.findAll();
        // 2. 循环转成 ES 文档并保存
        for (Course course : mysqlList) {
            CourseDoc doc = new CourseDoc();
            doc.setId(course.getCourseId());
            doc.setCourseName(course.getCourseName());
            doc.setDescription(course.getDescription());
            doc.setResourceType(course.getResourceType());
            doc.setCoverImg(course.getCoverImg());

            courseRepository.save(doc); // 存入 ES
        }

        return Result.success("同步成功，共同步了 " + mysqlList.size() + " 条数据");
    }
}
