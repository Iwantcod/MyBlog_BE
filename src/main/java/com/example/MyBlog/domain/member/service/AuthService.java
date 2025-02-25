package com.example.MyBlog.domain.member.service;

import com.example.MyBlog.domain.Util.JwtUtil;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {
    private final JwtUtil jwtUtil;
    @Autowired
    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    // refresh token 검증 및 두 가지 토큰 재발급
    public Cookie[] tokenRefresh(Cookie[] cookies) {
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh_token")) {
                    log.info("AuthService: get refresh token");
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if(refreshToken == null) {
            // Refresh Token 존재하지 않음.
            log.info("AuthService: Doesn't Exist Refresh Token");
            return null;
        }
        String uuid = jwtUtil.getId(refreshToken);
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        if(!jwtUtil.isExistRefresh(username, uuid)) {
            // Refresh Token 존재하지 않는다면 토큰 재발급 불가
            log.info("AuthService: timeout Refresh Token");
            return null;
        }

        Cookie[] newCookies = new Cookie[2];
        newCookies[0] = jwtUtil.createJwt(username, uuid); // new access token
        newCookies[1] = jwtUtil.createRefresh(username, uuid); // new refresh token
        // 두 개의 쿠키가 담긴 쿠키 배열을 반환
        return newCookies;
    }
}
