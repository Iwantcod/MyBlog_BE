package com.example.MyBlog.domain.likes.entity;

import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "LIKES")
public class Like {
    @Id @GeneratedValue @Column(name = "LIKE_ID")
    private Long id;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String memberUsername;

    @CreationTimestamp // INSERT 쿼리가 발생할 때, 현재 시간을 값으로 채워서 자동으로 쿼리를 생성
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public void setPost(Post post) {
        this.post = post;
        post.getLikes().add(this);
    }

    public void setMember(Member member) {
        this.member = member;
        this.memberUsername = member.getUsername();
        member.getLikes().add(this);
    }
}
