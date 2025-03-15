package com.example.MyBlog.domain.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Value("${app.client-url}")
    private String clientUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트(/**)에 대해 CORS 정책을 적용: 모든 API에 적용
                .allowedOriginPatterns(clientUrl+"*") // 특정 출처(origin)에 대해서만 CORS 허용
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}