package com.example.MyBlog.domain.Util;

import com.example.MyBlog.domain.member.entity.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final Long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Long expiration) {
        // 설정 파일에 입력된 시크릿 코드를 기반으로 HS256 방식으로 암호화
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        this.expiration = expiration;
    }

    // 토큰을 SecretKey로 파싱하여 Claims를 얻어내는 메소드
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

    public Boolean isExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date()); // 현재 시각과 비교하여 토큰이 만료되었는지를 판별
    }

    // jwt 생성 메소드
    public String createJwt(String username, String role) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 생성 시간
                .setExpiration((new Date(System.currentTimeMillis() + expiration))) // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 암호화 키
                .compact(); // JWT 문자열로 반환

    }

}
