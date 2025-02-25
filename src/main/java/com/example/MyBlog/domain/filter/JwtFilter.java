package com.example.MyBlog.domain.filter;

import com.example.MyBlog.domain.Util.JwtUtil;
import com.example.MyBlog.domain.member.DTO.AuthDTO;
import com.example.MyBlog.domain.member.details.JwtUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    // JWT 검증을 위한 필터
    // 요청 헤더 Authorization 키에 JWT가 존재하는 경우 검증하고, 강제로 SecurityContextHolder에 세션을 생성한다.
        // 이 세션은 StateLess 상태로 관리되므로, 해당 요청이 종료되면 같이 소멸한다.

    private final JwtUtil jwtUtil;
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override // Access Token 검증. Refresh Token 검증은 별도의 API 요청을 통해 이루어짐.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ExpiredJwtException {

        String accessToken = null;
        // 요청에 포함된 쿠키를 확인
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        // access token이 없으면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 엑세스 토큰 검증
        if (jwtUtil.isExpired(accessToken)) {
            // 만약 access token이 만료되었다면, refresh token을 이용하여 새로운 토큰 발급 로직이 필요
            // 이 부분은 별도의 리프레시 로직(예: /api/auth/refresh)을 구현하여 처리
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("authorization now");

        // 예외사항에 해당되지 않으면 아래를 실행
        setAuthentication(accessToken);
        filterChain.doFilter(request, response);
    }

    // SecurityContextHolder 세션에 인증된 사용자 추가(임시 일회성 세션)
    private void setAuthentication(String token) {
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // username, role, password(더미 비밀번호) 를 전달하기 위한 AuthDTO
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername(username);
        authDTO.setRoleType(role);
        // JWT를 이용한 인증에는 패스워드 정보가 필요없다.
        // UserDetails 인터페이스의 getPassword()를 구현하기 위한 더미 비밀번호
        authDTO.setPassword("tempPW");

        // UserDetails에 인증에 필요한 유저 정보 담기
        JwtUserDetails jwtUserDetails = new JwtUserDetails(authDTO);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(jwtUserDetails, null, jwtUserDetails.getAuthorities());

        // SecurityContextHolder 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}