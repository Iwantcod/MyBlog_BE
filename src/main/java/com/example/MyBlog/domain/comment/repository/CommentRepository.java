package com.example.MyBlog.domain.comment.repository;

import com.example.MyBlog.domain.comment.entity.Comment;
import com.example.MyBlog.domain.post.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.post.id = :postId")
    Page<Comment> findByPostIdPaging(@Param("postId") Long postId, Pageable pageable);

    @Modifying
    @Query("delete from Comment c where c.post.id = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);

}
