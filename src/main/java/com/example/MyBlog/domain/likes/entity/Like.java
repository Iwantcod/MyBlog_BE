package com.example.MyBlog.domain.likes.entity;

import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;

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

    private int counts;

    public void setPost(Post post) {
        this.post = post;
        post.getLikes().add(this);
    }

    public void setMember(Member member) {
        this.member = member;
        member.getLikes().add(this);
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }
}
