package com.example.MyBlog.domain.comment.entity;

import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JoinColumn(name = "parent_comment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parentComment; // 부모 댓글 id

    @Column(nullable = false)
    private int depth = 0; // 기본값 0

    @Column(nullable = false)
    private boolean isDeleted = false; // 삭제 여부를 나타내는 필드. 기본값 false

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> children = new ArrayList<>(); // 자식 댓글 정보

    // materialized path: 댓글의 계층 구조를 문자열로 표현 (예: 부모 식별자가 1, 자식 식별자가 3: "1.3")
    @Column(length = 255)
    private String threadPath;

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

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
        parentComment.children.add(this);
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setThreadPath(String threadPath) {
        this.threadPath = threadPath;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
