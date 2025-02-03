package com.example.MyBlog.domain.member.repository;

import com.example.MyBlog.domain.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String userName);

    // 유저네임을 해시로 변환한 값을 가지고 쿼리문을 수행
    @Query("select m from Member m where m.usernameHash = :hash")
    Optional<Member> findByUsernameHash(@Param("hash") byte[] hash);

    Optional<Member> findByUsernameAndPassword(String userName, String password);
    boolean existsByUsername(String username);
}
