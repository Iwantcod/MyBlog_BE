package com.example.MyBlog.domain.follow.entity;

import com.example.MyBlog.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "FOLLOWS")
public class Follow {
    @Id @GeneratedValue @Column(name = "FOLLOW_ID")
    private Long id;
    // member가 target을 팔로우하는 관계를 표현
    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @JoinColumn(name = "target_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member target;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setTarget(Member target) {
        this.target = target;
    }
}
