package com.example.MyBlog.domain.member.repository;

import com.example.MyBlog.domain.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String userName);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}
