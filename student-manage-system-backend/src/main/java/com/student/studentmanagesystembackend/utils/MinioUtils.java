package com.student.studentmanagesystembackend.utils;

import io.minio.http.Method; // 用于指定请求方法
import io.minio.GetPresignedObjectUrlArgs; // 用于生成预签名 URL
import org.springframework.beans.factory.annotation.Value;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class MinioUtils {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.bucket-name}")
    private String bucketName;

    private static final int PRESIGNED_URL_EXPIRY_DAYS = 7;

    private MinioClient minioClient;

    // 初始化 Minio 客户端
    @PostConstruct
    public void init(){
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    //上传文件
    public String upload(MultipartFile file){
        try{

            // 1. 生成唯一文件名 (uuid + 后缀)
            String originalFilename = file.getOriginalFilename();

            // 文件名校验，避免 NullPointerException
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new RuntimeException("文件上传失败：文件名为空。");
            }

            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID() + suffix;

            // 2. 上传到 MinIO
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 生成预签名 URL
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET) // 指定客户端将使用 GET 方法访问
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(PRESIGNED_URL_EXPIRY_DAYS, TimeUnit.DAYS) // 设置有效期
                            .build()
            );

            // 返回有时效性的访问 URL
            return presignedUrl;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("MinIO上传失败：" + e.getMessage());
        }
    }
}
