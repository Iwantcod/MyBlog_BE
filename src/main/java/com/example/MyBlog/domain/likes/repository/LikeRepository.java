package com.example.MyBlog.domain.likes.repository;

import com.example.MyBlog.domain.likes.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByMemberId(Long memberId);
    List<Like> findAllByPostId(Long postId);
    void deleteAllByMemberId(Long memberId);
    void deleteAllByPostId(Long postId);
}
