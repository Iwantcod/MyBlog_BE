package com.example.MyBlog.domain.member.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinDTO {
    private String name;
    private String password;
    private int age;
    private String username;
    // 유저 권한 타입은 기본값: USER
}
