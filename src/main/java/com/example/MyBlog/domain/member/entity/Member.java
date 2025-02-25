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
@Table(indexes = @Index(name = "idx_member_id", columnList = "MEMBER_ID"))
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

    private int followersCnt = 0;

    private int followingCnt = 0;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
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
    }

    public void setFollowersCnt(int followersCnt) {
        this.followersCnt = followersCnt;
    }

    public void setFollowingCnt(int followingCnt) {
        this.followingCnt = followingCnt;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

}
