package com.example.MyBlog.domain.post.entity;

import com.example.MyBlog.domain.comment.entity.Comment;
import com.example.MyBlog.domain.image.entity.Image;
import com.example.MyBlog.domain.likes.entity.Like;
import com.example.MyBlog.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "POSTS")
public class Post {
    @Id @GeneratedValue @Column(name = "POST_ID")
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    public void setMember(Member member) {
        this.member = member;
        member.getPosts().add(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
