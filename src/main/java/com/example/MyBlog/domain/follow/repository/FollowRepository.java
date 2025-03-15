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

    // 특정 유저의 팔로잉 수 조회
    @Query("select count(f) from Follow f where f.member.id = :memberId")
    Integer countByMemberId(@Param("memberId") Long memberId);

    // 특정 유저의 팔로워 수 조회
    @Query("select count(f) from Follow f where f.target.id = :memberId")
    Integer countByTargetId(@Param("memberId") Long memberId);


    @Query("select f from Follow f where f.member.id = :memberId and f.target.id = :targetId")
    Optional<Follow> findByMemberIdAndTargetId(@Param("memberId") Long memberId, @Param("targetId") Long targetId);

    // 특정 유저의 팔로잉 정보 삭제
    @Modifying
    @Query("delete from Follow f where f.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    // 특정 유저의 팔로워 정보 삭제
    @Modifying
    @Query("delete from Follow f where f.target.id = :targetId")
    void deleteByTargetId(@Param("targetId") Long targetId);
}
