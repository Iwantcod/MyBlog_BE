package com.example.MyBlog.domain.post.repository;

import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.post.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.member.id = :memberId")
    Page<Post> findAllByMemberId(@Param("memberId") Long memberId, Pageable pageable);



}
