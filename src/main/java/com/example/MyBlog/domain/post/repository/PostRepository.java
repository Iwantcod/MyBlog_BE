package com.example.MyBlog.domain.post.repository;

import com.example.MyBlog.domain.post.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.member.id = :memberId")
    List<Post> findAllByMemberId(@Param("memberId") Long memberId);


}
