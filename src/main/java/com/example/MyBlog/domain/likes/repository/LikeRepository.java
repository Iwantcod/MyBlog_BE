package com.example.MyBlog.domain.likes.repository;

import com.example.MyBlog.domain.likes.entity.Like;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("select l from Like l where l.member.id = :memberId")
    List<Like> findAllByMemberId(@Param("memberId") Long memberId);
    @Query("select l from Like l where l.post.id = :postId")
    Page<Like> findAllByPostId(@Param("postId") Long postId, Pageable pageable);


    @Query("select l from Like l where l.member.id = :memberId and l.post.id = :postId")
    Optional<Like> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Modifying
    @Query("delete from Like l where l.member.id = :memberId and l.post.id = :postId")
    void deleteByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);
}
