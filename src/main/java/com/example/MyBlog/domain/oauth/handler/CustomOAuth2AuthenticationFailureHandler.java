package com.example.MyBlog.domain.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomOAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2AuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.error("OAuth2 authentication failed: {}", exception.getMessage());
        String errorMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
        response.sendRedirect("/login?error=" + errorMessage);
    }
}
