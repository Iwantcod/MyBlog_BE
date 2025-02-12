package com.example.MyBlog.domain.comment.entity;

import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "COMMENTS")
public class Comment {
    @Id @GeneratedValue @Column(name = "COMMENT_ID")
    private Long id;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String memberUsername;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp // INSERT 쿼리가 발생할 때, 현재 시간을 값으로 채워서 자동으로 쿼리를 생성
    private LocalDateTime createdAt;

//    @Column(nullable = false)
//    @ColumnDefault("false")
//    private boolean isDeleted;

    public void setPost(Post post) {
        this.post = post;
        post.getComments().add(this);
    }

    public void setMember(Member member) {
        this.member = member;
        this.memberUsername = member.getUsername();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
