package com.example.MyBlog.domain.follow.repository;

import com.example.MyBlog.domain.follow.entity.Follow;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findAllByMemberId(Long memberId);
    List<Follow> findAllByTargetId(Long targetId);

    @Modifying
    @Query("delete from Follow f where f.member.id = :memberId and f.target.id = :targetId")
    void deleteByMemberIdAndTargetId(@Param("memberId") Long memberId, @Param("targetId") Long targetId);
    void deleteAllByMemberId(Long memberId);
    void deleteAllByTargetId(Long targetId);

    @Query("select f from Follow f where f.member.id = :memberId and f.target.id = :targetId")
    Optional<Follow> findByMemberIdAndTargetId(@Param("memberId") Long memberId, @Param("targetId") Long targetId);
}
