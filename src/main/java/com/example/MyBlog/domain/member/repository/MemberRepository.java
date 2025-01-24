package com.example.MyBlog.domain.member.repository;

import com.example.MyBlog.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String userName);

    Optional<Member> findByUsernameAndPassword(String userName, String password);
    boolean existsByUsername(String username);
}
