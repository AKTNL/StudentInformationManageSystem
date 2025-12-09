package com.student.studentmanagesystembackend.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.student.studentmanagesystembackend.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class FileController {
    // 这里要和 WebConfig 里的路径一致
    private static final String UPLOAD_PATH = "D:/upload/";

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file")MultipartFile file){
        if(file.isEmpty()){
            return Result.error("上传文件不能为空");
        }
        // 1. 生成唯一文件名 (防止同名覆盖)
        // 比如：原始名 a.jpg -> uuid.jpg
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename); //获取后缀
        String newFileName = IdUtil.simpleUUID() + "." + suffix;

        // 2. 保存文件到硬盘
        try {
            File dest = new File(UPLOAD_PATH + newFileName);
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdirs(); // 自动创建父目录
            }
            file.transferTo(dest);

            // 3. 返回可访问的 URL
            // 前端拿到这个 url 就能展示图片了
            String url = "http://localhost:8081/images/" + newFileName;
            return Result.success(url);
        }catch(IOException e){
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
}
