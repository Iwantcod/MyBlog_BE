package com.example.MyBlog.domain.follow.repository;

import com.example.MyBlog.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findAllByMemberId(Long memberId);
    List<Follow> findAllByTargetId(Long targetId);
    void deleteAllByMemberId(Long memberId);
    void deleteAllByTargetId(Long targetId);
}
