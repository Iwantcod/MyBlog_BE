package com.example.MyBlog.domain.filter;

import com.example.MyBlog.domain.Util.JwtUtil;
import com.example.MyBlog.domain.member.details.JwtUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    // 로그인을 위한 필터. 유저네임과 패스워드가 매칭되는지 검증 후 성공하면 JWT 반환

    @Value("${app.client-url}")
    private String clientUrl;

    // 토큰 검증을 수행할 AuthenticationManager 에게 정보를 넘겨주기 위해, 정보를 가진다.
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
        // 클라이언트 요청에서 유저네임과 비밀번호 추출
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 추출한 정보로 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // AuthenticationManager에게 넘겨 검증 절차 수행
        return authenticationManager.authenticate(authToken);
    }

    @Override // 로그인 성공 시 수행되는 메소드(JWT 발급)
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException {
        // (JwtUserDetails) auth.getPrincipal(): 인증된 사용자 정보를 JwtUserDetails 타입으로 가져온다.
        JwtUserDetails jwtUserDetails = (JwtUserDetails) auth.getPrincipal();

        // 얻어온 사용자 정보에서 유저네임과 권한 정보를 추출한다.
        String username = jwtUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = jwtUserDetails.getAuthorities();

        // 권한 리스트에서 첫 번째 요소만 취급한다.(내 서비스에서는 권한을 하나만 가질 수 있기 때문.)
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority grantedAuthority = iterator.next();
        // 가져온 권한 정보를 String으로 추출
        String role = grantedAuthority.getAuthority();

        // 최종적으로 얻어낸 정보를 토대로 access, refresh token 생성
        Cookie accessCookie =  jwtUtil.createJwt(username, role);
        Cookie refreshCookie = jwtUtil.createRefresh(username, role);

        // 응답에 쿠키 추가
        res.addCookie(accessCookie);
        res.addCookie(refreshCookie);
        res.sendRedirect(clientUrl);
    }


    @Override // 로그인 실패 시 수행되는 메소드
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) {
        res.setStatus(401);
    }
}
