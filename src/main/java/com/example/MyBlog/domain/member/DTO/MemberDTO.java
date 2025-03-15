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
    private int followerCount; // 나를 팔로우 하는 사람
    private int followingCount; // 내가 팔로우 하는 사람
    private RoleType roleType;
}
