package com.example.MyBlog.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트(/**)에 대해 CORS 정책을 적용: 모든 API에 적용
                .allowedOrigins("http://localhost:61314"); // CORS 요청을 특정 출처(origin)에 대해서만 허용
    }
}
