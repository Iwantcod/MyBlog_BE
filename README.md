# MyBlog_BE
JWT, OAuth를 적용한 SNS
> **개발기간: 2025.01.24. ~ 2025.03.24.**

---
## 📚 STACKS

<div>
  <img src="https://img.shields.io/badge/Java%2017-007396?style=for-the-badge&logo=Java&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot%203.4-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white" alt="Spring Boot 3.4"><br>
  <img src="https://img.shields.io/badge/MariaDB%2010-003545?style=for-the-badge&logo=MariaDB&logoColor=white" alt="MariaDB 10"><br>
  <img src="https://img.shields.io/badge/Nginx%20-339900?style=for-the-badge&logo=Nginx&logoColor=white" alt="nginx"><br>
  <img src="https://img.shields.io/badge/Raspberry%20Pi-A22846?style=for-the-badge&logo=RaspberryPi&logoColor=white" alt="Raspberry Pi 4b">
</div>

---
## 📝 API 명세서 및 ERD
> **ERD** : [ERD Cloud](https://www.erdcloud.com/d/wDtTQfztBWh6ruxqK) <br>
> **API** : [Swagger UI](https://iwantcod.github.io/MyBlog_API/) <br>

---
## 📌 개요
Spring Security, JWT, OAuth2, Redis를 공부하기 위한 SNS 백엔드 프로젝트입니다.

---
# 🔍 주요 기능
- ️게시글 작성
- 댓글 및 대댓글 작성

---
# 📕 성과
- Spring Security 컨셉 및 원리 이해
- Materialized Path 도입: 대댓글 계층형 구조를 고려한 조회
- CORS 에러에 대한 인지, 학습
- 삭제 쿼리문 호출 빈도수 최적화 (2N → 1)
- nginx 리버스 프록시 적용
- ssl 인증서 발급 및 https 활성화
- 부하테스트 후 Redis 캐싱 조회 성능 개선 (avg 4.02s → avg 1.47s)

---
# 아키텍처
## 디렉토리 구조
```bash
.
├── java
│   └── com
│       └── example
│           └── MyBlog
│               ├── MyBlogApplication.java
│               └── domain
│                   ├── Util
│                   │   └── JwtUtil.java
│                   ├── advice
│                   │   └── ExceptionAdvice.java
│                   ├── comment # 댓글 도메인
│                   │   ├── DTO
│                   │   │   ├── ...
│                   │   ├── controller
│                   │   │   └── CommentController.java
│                   │   ├── entity
│                   │   │   └── Comment.java
│                   │   ├── repository
│                   │   │   └── CommentRepository.java
│                   │   └── service
│                   │       └── CommentService.java
│                   ├── config # 각종 설정
│                   │   ├── CorsMvcConfig.java
│                   │   ├── CustomByteArrayRedisSerializer.java
│                   │   ├── JacksonConfig.java
│                   │   ├── RedisConfig.java # Redis 설정
│                   │   ├── SecurityConfig.java
│                   │   └── SwaggerConfig.java # Swagger UI
│                   ├── filter # Spring Security Filter Chain 커스터마이징
│                   │   ├── JwtFilter.java
│                   │   └── LoginFilter.java
│                   ├── follow # 팔로우 도메인
│                   │   ├── DTO
│                   │   │   ├── ...
│                   │   ├── controller
│                   │   │   └── FollowController.java
│                   │   ├── entity
│                   │   │   └── Follow.java
│                   │   ├── repository
│                   │   │   └── FollowRepository.java
│                   │   └── service
│                   │       └── FollowService.java
│                   ├── image # 이미지 도메인
│                   │   ├── DTO
│                   │   │   ├── ...
│                   │   ├── controller
│                   │   │   └── ImageController.java
│                   │   ├── entity
│                   │   │   └── Image.java
│                   │   ├── repository
│                   │   │   └── ImageRepository.java
│                   │   └── service
│                   │       └── ImageService.java
│                   ├── likes # 좋아요 도메인
│                   │   ├── DTO
│                   │   │   ├── ...
│                   │   ├── controller
│                   │   │   └── LikesController.java
│                   │   ├── entity
│                   │   │   └── Like.java
│                   │   ├── repository
│                   │   │   └── LikeRepository.java
│                   │   └── service
│                   │       └── LikesService.java
│                   ├── member # 회원 도메인
│                   │   ├── DTO
│                   │   │   ├── ...
│                   │   ├── controller
│                   │   │   ├── AuthController.java
│                   │   │   └── MemberController.java
│                   │   ├── details
│                   │   │   └── JwtUserDetails.java
│                   │   ├── entity
│                   │   │   ├── Member.java
│                   │   │   └── RoleType.java
│                   │   ├── repository
│                   │   │   └── MemberRepository.java
│                   │   └── service
│                   │       ├── AuthService.java
│                   │       ├── JwtUserDetailsService.java
│                   │       └── MemberService.java
│                   ├── oauth # OAuth2 설정 및 성공/실패 핸들러
│                   │   ├── details
│                   │   │   ├── CustomOauth2UserDetails.java
│                   │   │   ├── GoogleUserDetails.java
│                   │   │   └── OAuth2UserInfo.java
│                   │   ├── handler
│                   │   │   ├── CustomOAuth2AuthenticationFailureHandler.java
│                   │   │   └── CustomOAuth2AuthenticationSuccessHandler.java
│                   │   └── service
│                   │       └── CustomOauth2UserService.java
│                   └── post # 게시글 도메인
│                       ├── DTO
│                       │   ├── ...
│                       ├── controller
│                       │   └── PostController.java
│                       ├── entity
│                       │   └── Post.java
│                       ├── repository
│                       │   └── PostRepository.java
│                       └── service
│                           └── PostService.java
└── resources
    ├── application.yml
    ├── static
    └── templates
