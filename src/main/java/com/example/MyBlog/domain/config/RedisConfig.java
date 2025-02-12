package com.example.MyBlog.domain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    // redis 설정

    @Value("${app.server-host}")
    private String serverHost;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // redis 연결 설정
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(serverHost, 6379)
        );
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        // Redis 키 및 값 설정
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
