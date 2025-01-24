package com.example.MyBlog.domain.image.repository;

import com.example.MyBlog.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByPostId(Long postId);
    void deleteAllByPostId(Long postId);
}
