package com.example.MyBlog.domain.member.controller;

import com.example.MyBlog.domain.member.DTO.CompleteDTO;
import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.service.AuthService;
import com.example.MyBlog.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth API", description = "인증 토큰 필요 X, 로그인 요청(form 방식 요청): /api/auth/login")
public class AuthController {
    private final MemberService memberService;
    private final AuthService authService;

    @Value("${app.client-url}")
    private String clientUrl;

    @Autowired
    public AuthController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    // 회원가입
    @PostMapping("/join")
    @Operation(summary = "회원 가입")
    public ResponseEntity<?> join(@RequestBody JoinDTO joinDTO) {
        if (memberService.join(joinDTO)) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, clientUrl+"/auth/login").build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    // 유저네임 중복확인
    @GetMapping("/check-username/{username}")
    @Operation(summary = "유저네임 중복 체크")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        if(memberService.isDuplicationUsername(username)) {
            return ResponseEntity.status(409).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    // OAuth2를 통한 회원가입 시 유저네임만 저장
    @PostMapping("/complete")
    @Operation(summary = "OAuth2를 통한 회원가입 이후 유저네임만 추가로 저장")
    public ResponseEntity<?> completeProfile(@RequestBody CompleteDTO completeDTO, HttpServletRequest request) {
        Long memberId = (Long) request.getSession().getAttribute("incompleteMemberId");
        request.getSession().removeAttribute("incompleteMemberId");
        if(memberId == null) {
            return ResponseEntity.status(404).build();
        }

        if(memberService.completeMember(memberId, completeDTO.getUsername())) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, clientUrl).build();
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    // Refresh Token을 받아 토큰 검증을 하고, 새 토큰 반환
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token 재발급", description = "Access Token 또한 재발급한다.")
    public ResponseEntity<?> tokenRefresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = authService.tokenRefresh(request.getCookies());

        if(cookies != null) {
            response.addCookie(cookies[0]); // new access token
            response.addCookie(cookies[1]); // new refresh token
            return ResponseEntity.ok().build();
        } else {
            // 리프레쉬 토큰 발급 실패 혹은 만료 시, 로그인 페이지로 리다이렉션
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, clientUrl + "/auth/login").build();
        }
    }

}
