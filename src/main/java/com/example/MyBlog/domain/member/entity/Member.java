package com.example.MyBlog.domain.member.entity;

import com.example.MyBlog.domain.comment.entity.Comment;
import com.example.MyBlog.domain.follow.entity.Follow;
import com.example.MyBlog.domain.likes.entity.Like;
import com.example.MyBlog.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = @Index(name = "idx_member_usernameHash", columnList = "USERNAME_HASH"))
@Getter
public class Member {
    @Id @GeneratedValue @Column(name = "MEMBER_ID")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType roleType = RoleType.ROLE_USER; // 기본값: USER(일반 사용자)

    @Column(nullable = false, unique = true, name = "USERNAME")
    private String username; // 아이디 역할


    // 문자열 유저네임을 MD5 해시로 변환하여 도출된 바이너리 값을 저장하는 별도의 컬럼(jwt access token을 이용한 인증에서 사용)
    // MD5 방식은 단방향 해싱이므로, 해시 값에서 문자열로 다시 변환할 수 없다.(참고)
    @Column(nullable = false, columnDefinition = "BINARY(16)", unique = true, name = "USERNAME_HASH")
    private byte[] usernameHash;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Follow> follows = new ArrayList<>();

    @OneToMany(mappedBy = "target", cascade = CascadeType.REMOVE)
    private List<Follow> targets = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setUsername(String username) {
        this.username = username;
        this.usernameHash = DigestUtils.md5Digest(username.getBytes(StandardCharsets.UTF_8));
        // 유저네임 해시코드는 유저네임 저장할때 한번에 저장
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

}
