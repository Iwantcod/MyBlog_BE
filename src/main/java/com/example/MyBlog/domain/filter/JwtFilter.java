package com.example.MyBlog.domain.filter;

import com.example.MyBlog.domain.Util.JwtUtil;
import com.example.MyBlog.domain.member.DTO.AuthDTO;
import com.example.MyBlog.domain.member.details.JwtUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request 헤더에서 "Authorization" 키의 값(JWT)을 찾는다.
        String authorization = request.getHeader("Authorization");

        // 키의 값이 존재하고, 해당 키의 값이 "Bearer "로 시작하면(공백 포함) JWT를 가지고 있다는 의미이다.


        // 키의 값이 존재하지 않거나 값이 "Bearer "로 시작하지 않는 경우는 잘못된 값이다.
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");
            filterChain.doFilter(request, response);
            return; // 이 조건이 맞으면 메소드 종료(break) - 이것은 필수로 해줘야 한다.
        }

        System.out.println("authorization now");

        String token = authorization.split(" ")[1]; // JWT에 해당하는 부분만 따로 가져온다.

        // 토큰이 만료된 경우, 인가를 하지 않는다.
        if(jwtUtil.isExpired(token)) {
            System.out.println("Token Expired");
            filterChain.doFilter(request, response);
            return; // 이 조건이 맞으면 메소드 종료(break) - 이것은 필수로 해줘야 한다.
        }

        // 예외사항에 해당되지 않으면 아래를 실행

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername(username);
        authDTO.setRoleType(role);
        // JWT를 이용한 인증에는 패스워드 정보가 필요없다.
        // 그러나 공백으로 두면 불필요한 쿼리문이 발생할 수도 있으므로 임시 비밀번호 아무거나 설정하자.
        authDTO.setPassword("tempPW");


        // UserDetails에 인증에 필요한 유저 정보 담기
        JwtUserDetails jwtUserDetails = new JwtUserDetails(authDTO);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(jwtUserDetails, null, jwtUserDetails.getAuthorities());

        // SecurityContextHolder 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}