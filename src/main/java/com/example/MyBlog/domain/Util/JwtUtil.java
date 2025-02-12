package com.example.MyBlog.domain.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final Long expiration;

    private final SecretKey refreshKey;
    private final Long refreshExpiration;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Long expiration,
                   @Value("${jwt.refresh-secret}") String refreshSecret, @Value("${jwt.refresh-expiration}") Long refreshExpiration,
                   RedisTemplate<String, String> redisTemplate) {
        // 설정 파일에 입력된 시크릿 코드를 기반으로 HS256 방식으로 암호화
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        this.refreshKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());

        this.expiration = expiration; // Access Token Expiration
        this.refreshExpiration = refreshExpiration; // Refresh Token Expiration
        this.redisTemplate = redisTemplate;
    }

    // 토큰을 SecretKey로 파싱하여 Claims를 얻어내는 메소드: 토큰 검증
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // SecretKey 전달
                .build()
                .parseClaimsJws(token) // 토큰 파싱
                .getBody(); // Claims 얻기
    }

    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public String getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    // uuid Getter
    public String getId(String token) {
        Claims claims = parseToken(token);
        return claims.getId();

    }

    public boolean isExistRefresh(String username, String uuid) {
        String requestValue = redisTemplate.opsForValue().get("refresh:" + username);
        // 클라이언트 요청에 담긴 username을 키로 하여 저장된 uuid 값이 존재하고, 그 uuid값이 Redis에 저장된 값과 동일하다면 true
        if(requestValue != null){
            return requestValue.equals(uuid);
        }
        return false;
    }

    public Boolean isExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date()); // 현재 시각과 비교하여 토큰이 만료되었는지를 판별
    }


    // Access jwt 생성 메소드
    public String createJwt(String username, String role) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .setId(UUID.randomUUID().toString()) // uuid를 사용하여 jwt의 식별자를 부여
                .setIssuedAt(new Date(System.currentTimeMillis())) // 생성 시간
                .setExpiration((new Date(System.currentTimeMillis() + expiration))) // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 암호화 키
                .compact(); // JWT 문자열로 반환
    }

    // Refresh jwt 생성 메소드(Redis에 저장 및 반환)
    public String createRefresh(String username, String role) {

        String uuid = UUID.randomUUID().toString();
        String refreshToken = Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .setId(uuid) // uuid를 사용하여 jwt의 식별자를 부여
                .setIssuedAt(new Date(System.currentTimeMillis())) // 생성 시간
                .setExpiration((new Date(System.currentTimeMillis() + refreshExpiration))) // 만료 시간
                .signWith(refreshKey, SignatureAlgorithm.HS256) // 암호화 키
                .compact(); // JWT 문자열로 반환

        // refresh token을 Redis에 저장 (이미 존재하는 키의 값을 사용하여 저장하면 자동으로 덮어쓰기가 된다)
        redisTemplate.opsForValue().set("refresh:" + username, uuid, refreshExpiration, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    public void deleteRefresh(String username) {
        redisTemplate.delete("refresh:" + username);
    }

}
