package com.student.studentmanagesystembackend.controller;

import com.student.studentmanagesystembackend.annotation.AuthCheck;
import com.student.studentmanagesystembackend.common.Result;
import com.student.studentmanagesystembackend.utils.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
public class FileController {

    @Autowired
    private MinioUtils minioUtils;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp",
        "video/mp4", "video/webm",
        "application/pdf"
    );

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    @PostMapping("/upload")
    @AuthCheck(requireLogin = true)
    public Result<String> upload(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return Result.error("上传文件不能为空");
        }

        if(file.getSize() > MAX_FILE_SIZE){
            return Result.error("文件大小不能超过100MB");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if(originalFilename != null && originalFilename.contains(".")){
            suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if(contentType == null || !ALLOWED_TYPES.contains(contentType)){
            if(!isAllowedExtension(suffix)){
                return Result.error("不支持的文件类型: " + contentType);
            }
        }

        try {
            String url = minioUtils.upload(file);
            return Result.success(url);
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    private boolean isAllowedExtension(String suffix){
        List<String> allowedExtensions = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp",
            ".mp4", ".webm",
            ".pdf"
        );
        return allowedExtensions.contains(suffix);
    }
}
