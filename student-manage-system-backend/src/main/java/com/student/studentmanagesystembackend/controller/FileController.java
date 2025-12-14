package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.utils.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class FileController {

    @Autowired
    private MinioUtils minioUtils;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file")MultipartFile file){
        if(file.isEmpty()){
            return Result.error("上传文件不能为空");
        }

        try {
            // 调用 MinIO 工具类上传
            String url = minioUtils.upload(file);
            return Result.success(url);
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}
