package com.example.MyBlog.domain.member.service;

import com.example.MyBlog.domain.member.DTO.MemberDTO;
import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public MemberDTO getMemberById(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if(member.isPresent()) {
            return toDTO(member.get());
        } else {
            return null;
        }
    }

    public MemberDTO updateMember(JoinDTO joinDTO) {
        Member member = new Member();
        String encodePassword = bCryptPasswordEncoder.encode(joinDTO.getPassword());
        member.setUsername(joinDTO.getUsername());
        member.setPassword(encodePassword);
        member.setAge(joinDTO.getAge());
        member.setName(joinDTO.getName());
        Member savedMember = memberRepository.save(member);
        return toDTO(savedMember);
    }

    public boolean deleteMemberById(Long id) {
        memberRepository.deleteById(id);
        return true;
    }

}
