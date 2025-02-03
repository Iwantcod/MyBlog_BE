package com.example.MyBlog.domain.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token의 uuid를 저장. key는 username, 유효시간 단위: ms
    public void setRefreshTokenUuid(String username, String uuid, Long refreshExpiration) {
        redisTemplate.opsForValue().set(username, uuid, refreshExpiration, TimeUnit.MILLISECONDS);
    }

    // Refresh Token uuid 조회. username으로 조회.
    public String getRefreshTokenUuid(String username) {
        return redisTemplate.opsForValue().get(username);
    }

    // Refresh Token uuid 제거(로그아웃 시)
    public void deleteRefreshTokenUuid(String username) {
        redisTemplate.delete(username);
    }
}
