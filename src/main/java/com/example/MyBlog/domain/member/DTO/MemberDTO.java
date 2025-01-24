package com.example.MyBlog.domain.member.DTO;

import com.example.MyBlog.domain.member.entity.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDTO {
    private Long id;
    private String name;
    private int age;
    private String username;
    private RoleType roleType;
}
