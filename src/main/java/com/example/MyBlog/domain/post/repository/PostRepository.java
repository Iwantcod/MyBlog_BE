package com.example.MyBlog.domain.post.repository;

import com.example.MyBlog.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByMemberId(Long memberId);
    void deleteAllByMemberId(Long memberId);
}
