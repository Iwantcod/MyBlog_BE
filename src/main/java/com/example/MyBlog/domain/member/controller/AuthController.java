package com.example.MyBlog.domain.member.controller;

import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.service.AuthService;
import com.example.MyBlog.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final MemberService memberService;
    private final AuthService authService;

    @Value("${app.server-url}")
    private String serverUrl;

    @Autowired
    public AuthController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDTO joinDTO) {
        if (memberService.join(joinDTO)) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, serverUrl + "/login").build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    // 유저네임 중복확인
    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        if(memberService.isDuplicationUsername(username)) {
            return ResponseEntity.status(409).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    // Refresh Token을 받아 토큰 검증을 하고, 새 토큰 반환
    @PostMapping("/refresh")
    public ResponseEntity<?> tokenRefresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = authService.tokenRefresh(request.getCookies());

        if(cookies != null) {
            response.addCookie(cookies[0]); // new access token
            response.addCookie(cookies[1]); // new refresh token
            return ResponseEntity.ok().build();
        } else {
            // 리프레쉬 토큰 발급 실패 혹은 만료 시, 로그인 페이지로 리다이렉션
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, serverUrl + "/login").build();
        }
    }
}
