package com.example.MyBlog.domain.member.DTO;

import com.example.MyBlog.domain.member.entity.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthDTO {
    // JWT를 이용한 인증을 위해 유저 정보를 전달할 때 사용되는 DTO
    private Long memberId; // 회원 식별자
    private String username;
    private String password;
    // JWT에서는 ENUM 타입을 사용하면 안 된다. 따라서 String 으로 변환하여 검색
    private String roleType; // 기본값은 USER. 로그인 창에서 유저인지 관리자인지 체크박스 등을 통해 값을 입력하는 형태
}
