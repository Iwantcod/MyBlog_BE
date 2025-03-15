package com.example.MyBlog.domain.member.repository;

import com.example.MyBlog.domain.member.DTO.AuthDTO;
import com.example.MyBlog.domain.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Query("delete from Member m where m.id = :userId")
    void deleteByUsername(@Param("userId") Long userId);

    Optional<Member> findByoAuthId(String oAuthId);
}
