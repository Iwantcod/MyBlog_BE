package com.example.MyBlog.domain.oauth.handler;

import com.example.MyBlog.domain.Util.JwtUtil;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.service.MemberService;
import com.example.MyBlog.domain.oauth.details.CustomOauth2UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    @Value("${app.client-url}")
    private String clientUrl;

    public CustomOAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        CustomOauth2UserDetails customOauth2UserDetails = (CustomOauth2UserDetails) oAuth2User;
        Member member = customOauth2UserDetails.getMember();

        // google OAuth2 인증 성공 시 해당 유저가 회원가입을 한 경우 예외처리
        if (member.getUsername() == null) {
            request.getSession().setAttribute("incompleteMemberId", member.getId());
            response.sendRedirect(clientUrl + "/auth/join/complete"); // 회원가입인 경우, 유저네임 생성하는 창으로 리다이렉션
        } else { // 기존 유저인 경우 jwt 발급
            String username = member.getUsername();
            Collection<? extends GrantedAuthority> authorities = customOauth2UserDetails.getAuthorities();

            // 권한 리스트에서 첫 번째 요소만 취급한다.(내 서비스에서는 권한을 하나만 가질 수 있기 때문.)
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority grantedAuthority = iterator.next();
            // 가져온 권한 정보를 String으로 추출
            String role = grantedAuthority.getAuthority();

            // 최종적으로 얻어낸 정보를 토대로 access, refresh token 생성
            Cookie accessCookie = jwtUtil.createJwt(username, role);
            Cookie refreshCookie = jwtUtil.createRefresh(username, role);

            // 응답에 쿠키 추가
            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);
            response.sendRedirect(clientUrl);
        }
    }

}
