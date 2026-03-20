package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.annotation.AuthCheck;
import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.constant.UserConstants;
import com.student.studentmanagesystembackend.entity.Course;
import com.student.studentmanagesystembackend.entity.CourseDoc;
import com.student.studentmanagesystembackend.mapper.CourseMapper;
import com.student.studentmanagesystembackend.repository.CourseRepository;
import com.student.studentmanagesystembackend.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourseController {
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/courses")
    @AuthCheck(requireLogin = true)
    public Result<List<Course>> list(){
        return Result.success(courseMapper.findAll());
    }

    @PostMapping("/courses")
    @AuthCheck(roles = {UserConstants.ROLE_ADMIN})
    public Result<String> add(@RequestBody Course course){
        courseMapper.insert(course);

        CourseDoc doc = new CourseDoc();
        doc.setId(course.getCourseId());
        doc.setCourseName(course.getCourseName());
        doc.setDescription(course.getDescription());
        doc.setCoverImg(course.getCoverImg());

        courseRepository.save(doc);

        WebSocketServer.sendInfo("新课上架：" + course.getCourseName());

        return Result.success("发布成功");
    }

    @DeleteMapping("courses/{id}")
    @AuthCheck(roles = {UserConstants.ROLE_ADMIN})
    public Result<String> delete(@PathVariable Long id){
        courseMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/courses/search")
    @AuthCheck(requireLogin = true)
    public Result<List<CourseDoc>> search(@RequestParam String keyword){
        List<CourseDoc> list = courseRepository.findByCourseNameOrDescription(keyword, keyword);
        return Result.success(list);
    }

    @GetMapping("/courses/sync")
    @AuthCheck(roles = {UserConstants.ROLE_ADMIN})
    public Result<String> syncData(){
        List<Course> mysqlList = courseMapper.findAll();
        for (Course course : mysqlList) {
            CourseDoc doc = new CourseDoc();
            doc.setId(course.getCourseId());
            doc.setCourseName(course.getCourseName());
            doc.setDescription(course.getDescription());
            doc.setResourceType(course.getResourceType());
            doc.setCoverImg(course.getCoverImg());

            courseRepository.save(doc);
        }

        return Result.success("同步成功，共同步了 " + mysqlList.size() + " 条数据");
    }
}
