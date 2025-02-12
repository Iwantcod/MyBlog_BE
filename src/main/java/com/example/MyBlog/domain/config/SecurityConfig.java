package com.example.MyBlog.domain.config;

import com.example.MyBlog.domain.Util.JwtUtil;
import com.example.MyBlog.domain.filter.JwtFilter;
import com.example.MyBlog.domain.filter.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
public class SecurityConfig {
    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    @Value("${app.server-url}")
    private String serverUrl;

    @Value("${app.client-url}")
    private String clientUrl;

    @Autowired
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 필터를 커스텀한 LoginFilter에서 '기본 로그인 요청 경로'를 "/api/auth/login"으로 변경
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("/api/auth/login");


        // HttpSecurity 설정
        // 커스텀필터는 오버라이딩되지 않는다. 커스텀된것이 원본필터보다 직전에 실행될 뿐이다.
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors((corsCustom) -> corsCustom.configurationSource(new CorsConfigurationSource() {
                    // CORS(Cross-Origin Resource Sharing): 다른 도메인에서 오는 요청을 허용하거나 제한하는 메커니즘
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        // 허용할 출처(Origin)를 설정합니다. 여기서는 "http://localhost:61314"만 허용됩니다.
                        // 즉, 이 도메인에서 오는 요청만을 처리합니다.
                        // Collections.singletonList() == 단 하나의 요소만 가지는 불변(final) 리스트 생성
                        config.setAllowedOrigins(Collections.singletonList(clientUrl));

                        // 허용할 HTTP 메서드를 설정합니다. "*"는 모든 HTTP 메서드(GET, POST, PUT, DELETE 등)를 허용합니다.
                        config.setAllowedMethods(Collections.singletonList("*"));

                        // 요청에 자격 증명(Credentials, 예: 쿠키, HTTP 인증 등)을 포함하도록 허용합니다.
                        // 이 설정이 true일 경우, `Access-Control-Allow-Credentials` 헤더가 true로 설정됩니다.
                        config.setAllowCredentials(true);

                        // 허용할 요청 헤더를 설정합니다. "*"은 모든 헤더를 허용한다는 의미입니다.
                        // 필요에 따라 특정 헤더만 허용할 수도 있습니다.
                        config.setAllowedHeaders(Collections.singletonList("*"));

                        // CORS 응답 캐시 시간(초)을 설정합니다. 이 값은 클라이언트가 얼마나 오랫동안 이 CORS 정책을 캐시할지를 결정합니다.
                        // 여기서는 3600초, 즉 1시간 동안 캐시됩니다.
                        config.setMaxAge(3600L);

                        // CORS 응답에서 노출될 헤더를 설정합니다. 클라이언트에서 `Authorization`, 'Refresh' 헤더에 접근할 수 있도록 허용합니다.
                        // 예를 들어, 인증 토큰 등을 클라이언트에서 읽어야 할 때 유용합니다.
                        // List를 인자로 받는다.
                        config.setExposedHeaders(List.of("Authorization", "Refresh"));
                        return config;
                    }
                }))
                .authorizeHttpRequests((auth) ->
                        auth.requestMatchers(HttpMethod.GET, "/**").permitAll() // 모든 GET 요청(조회)은 인증 및 인가 없이 가능
                                .requestMatchers("/api/auth/**").permitAll() // 인증을 요청하는 api 및 회원가입, 로그인 페이지는 무인가 접근 허용
                                .requestMatchers("/api/admin/**").hasRole("ADMIN") // admin 관련 페이지와 api 요청은 ADMIN만 가능
                                .anyRequest().authenticated())
                    .formLogin(AbstractHttpConfigurer::disable)
                    .logout(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class) // LoginFilter 앞에 JwtFilter를 위치시킨다.
                    .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class) // 두번째 인자의 필터 순서에 LoginFilter를 추가한다.(바로 앞에 삽입된다)
                    .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT 인증방식을 위해 STATELESS 설정 (세션 미사용)
        return http.build();
    }
}
