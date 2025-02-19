package com.example.MyBlog.domain.image.repository;

import com.example.MyBlog.domain.image.entity.Image;
import com.example.MyBlog.domain.post.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("select i from Image i where i.post.id = :postId")
    List<Image> findAllByPostId(@Param("postId") Long postId);

    void deleteAllByPost(Post post);

    @Modifying
    @Query("delete from Image i where i.post.id = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);

    List<Image> findAllByPost(Post post);
}
