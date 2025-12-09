package com.student.studentmanagesystembackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**") // 允许所有的接口被访问
                .allowedOriginPatterns("*")  // 允许所有的来源(前端地址)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方式
                .allowCredentials(true) // 允许携带cookie
                .maxAge(3600); // 1小时内不需要再预检
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        /*  访问 http://localhost:8081/images/xxx.jpg
            去 D:/upload/ 目录下找 xxx.jpg
            file:后面是实际存图路径，最后一定要带
         */
        registry.addResourceHandler("/images/**").addResourceLocations("file:D:/upload/");
    }
}
