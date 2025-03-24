package com.example.MyBlog.domain.post.entity;

import com.example.MyBlog.domain.comment.entity.Comment;
import com.example.MyBlog.domain.image.entity.Image;
import com.example.MyBlog.domain.likes.entity.Like;
import com.example.MyBlog.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "POSTS")
public class Post {
    @Id @GeneratedValue @Column(name = "POST_ID")
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String memberUsername;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp // INSERT 쿼리가 발생할 때, 현재 시간을 값으로 채워서 자동으로 쿼리를 생성
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private int commentsCount = 0; // 댓글 수 카운트

    private int likesCount = 0; // 좋아요 수 카운트

    private int imagesCount = 0; // 이미지 수 카운트: 0이면 Image 테이블 조회를 하지 않는다.

    private Long viewsCount = 0L; // 조회수: redis에서 카운팅했다가 동기화

//    @Column(nullable = false)
//    @ColumnDefault("false")
//    private boolean isDeleted;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post") // 외부 스토리지를 이용하므로 JPA에게 cascade를 위임하지 않고 직접 로직을 구상하여 사용
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();

    public void setMember(Member member) {
        this.member = member;
        this.memberUsername = member.getUsername();
        member.getPosts().add(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    public void setImagesCount(int imagesCount) { this.imagesCount = imagesCount; }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
}
