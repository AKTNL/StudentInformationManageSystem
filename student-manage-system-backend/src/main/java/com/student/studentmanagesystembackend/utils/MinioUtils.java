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

    public String getPresignedUrl(String objectName){
        try{
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET) // 指定客户端将使用 GET 方法访问
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(PRESIGNED_URL_EXPIRY_DAYS, TimeUnit.DAYS) // 设置有效期
                            .build()
            );
        }catch (Exception e){
            e.printStackTrace();
            throw  new RuntimeException("MinIO获取预签名URL失败：" + e.getMessage());
        }
    }

    //上传文件
    public String upload(MultipartFile file){

        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + suffix;

            // 【新增】手动判断并纠正 Content-Type
            String contentType = file.getContentType();
            if (suffix.equalsIgnoreCase(".pdf")) {
                contentType = "application/pdf";
            } else if (suffix.equalsIgnoreCase(".mp4")) {
                contentType = "video/mp4";
            }

            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType) // 【使用修正后的类型】
                            .build()
            );

            return endpoint + "/" + bucketName + "/" + objectName;

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("MinIO上传失败：" + e.getMessage());
        }
    }
}
