package com.example.MyBlog.domain.member.service;

import com.example.MyBlog.domain.member.DTO.MemberDTO;
import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private MemberDTO toDTO(Member member) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(member.getId());
        memberDTO.setName(member.getName());
        memberDTO.setUsername(member.getUsername());
        memberDTO.setAge(member.getAge());
        memberDTO.setRoleType(member.getRoleType());
        return memberDTO;
    }


    // 닉네임 중복 검사(true: 중복, false: 중복이 아님)
    public boolean isDuplicationUsername(String inputUserName) {
        return memberRepository.existsByUsername(inputUserName);
    }

    public MemberDTO join(JoinDTO joinDTO) {
        if(isDuplicationUsername(joinDTO.getUsername())) {
            // 유저네임 중복된 경우 null 반환
            return null;
        } else {
            Member member = new Member();
            String encodePassword = bCryptPasswordEncoder.encode(joinDTO.getPassword());

            member.setUsername(joinDTO.getUsername());
            member.setPassword(encodePassword);
            member.setAge(joinDTO.getAge());
            member.setName(joinDTO.getName());
            Member savedMember = memberRepository.save(member);
            return toDTO(savedMember);
        }
    }

//    public void logout()

    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if(member.isPresent()) {
            return toDTO(member.get());
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public MemberDTO getMemberByUsernameHash(String username) {
        // 문자열 유저네임을 MD5 해시로 변환
        byte[] usernameHash = DigestUtils.md5Digest(username.getBytes(StandardCharsets.UTF_8));
        Optional<Member> member = memberRepository.findByUsernameHash(usernameHash);
        if(member.isPresent()) {
            return toDTO(member.get());
        } else {
            return null;
        }
    }

    @Transactional
    public MemberDTO updateMember(JoinDTO joinDTO) {
        Optional<Member> member = memberRepository.findById(joinDTO.getId());
        if(isDuplicationUsername(joinDTO.getUsername()) || member.isEmpty()) {
            // 유저네임 중복된 경우 혹은 회원정보를 찾지 못한경우 null 반환
            return null;
        } else {
            String encodePassword = bCryptPasswordEncoder.encode(joinDTO.getPassword());
            member.get().setUsername(joinDTO.getUsername());
            member.get().setPassword(encodePassword);
            member.get().setAge(joinDTO.getAge());
            member.get().setName(joinDTO.getName());
            Member updatedMember = memberRepository.save(member.get());
            return toDTO(updatedMember);
        }
    }

    public boolean deleteMemberById(Long id) {
        memberRepository.deleteById(id);
        return true;
    }

}
