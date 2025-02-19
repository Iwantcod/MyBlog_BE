package com.example.MyBlog.domain.member.service;

import com.example.MyBlog.domain.Util.JwtUtil;
import com.example.MyBlog.domain.member.DTO.MemberDTO;
import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import com.example.MyBlog.domain.post.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final PostService postService;

    @Autowired
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil, PostService postService) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.postService = postService;
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

    @Transactional
    public boolean join(JoinDTO joinDTO) {
        if(isDuplicationUsername(joinDTO.getUsername()) || joinDTO.getAge() <= 0) {
            // 유저네임 중복된 경우와 Age가 0 이하인 경우 null 반환
            log.error("Username already exist or User Age invalid");
            return false;
        } else {
            Member member = new Member();
            String encodePassword = bCryptPasswordEncoder.encode(joinDTO.getPassword());

            member.setUsername(joinDTO.getUsername());
            member.setPassword(encodePassword);
            member.setAge(joinDTO.getAge());
            member.setName(joinDTO.getName());
            memberRepository.save(member);
            log.info("JOIN member SUCCESS: Member Username: {}", member.getUsername());
            return true;
        }
    }


    // 로그아웃 메소드. 이 메소드를 호출할 때 사용된 jwt 내부의 유저네임을 추출하고, 그 유저네임에 대응하는 리프레쉬토큰을 redis에서 제거
    // 유저 탈퇴, 유저 업데이트 시에도 호출된다.
    public boolean logout() {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        try{
            jwtUtil.deleteRefresh(authUsername);
            log.info("LOGOUT member SUCCESS: Member Username: {}", authUsername);
            return true;
        } catch (Exception e) {
            log.error("LOGOUT member ERROR: Member Username: {}", authUsername);
            return false;
        }
    }

    @Transactional(readOnly = true)
    public MemberDTO getMemberByUsername(String username) { // 특정 유저 검색 시 활용
        Optional<Member> member = memberRepository.findByUsername(username);
        if(member.isPresent()) {
            return toDTO(member.get());
        }
        log.error("GET Member By Username FAIL: {}", username);
        return null;
    }

    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) { // 자주 조회하는 유저(ex: 본인 등)는 id로 조회하면 좋음.(성능)
        Optional<Member> member = memberRepository.findById(id);
        if(member.isPresent()) {
            return toDTO(member.get());
        } else {
            log.error("GET Member By Member id FAIL: {}", id);
            return null;
        }
    }


    @Transactional // 비밀번호를 포함한 정보를 넘겨받아야 하기에, JoinDTO로 받는다. 그러나 반환정보에는 비밀번호가 포함되면 안되므로 MemberDTO 반환.
    public boolean updateMemberById(JoinDTO joinDTO, Long userId) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // userId에 해당하는 유저 정보에 JoinDTO에 담긴 정보를 반영하여 부분갱신한다.(PATCH 방식)

        // 유저네임을 수정하는 경우이고, 수정하려는 유저네임이 중복되는 경우에는 false
        if(joinDTO.getUsername() != null && isDuplicationUsername(joinDTO.getUsername())) {
            log.error("Username already exist or Username is Empty. Username: {}", joinDTO.getUsername());
            return false;
        }

        // 인증 정보의 유저네임에 해당하는 유저의 정보만 수정가능
        Optional<Member> member = memberRepository.findById(userId);
        if(member.isEmpty()) {
            log.error("Member is Empty. Member id: {}", userId);
            return false;
        } else if (!member.get().getUsername().equals(authUsername)) {
            // 변경 대상 유저의 식별자로 찾아낸 유저네임과 jwt 토큰의 유저네임이 서로 일치하지 않으면 수정 불가
            log.error("Username does not match Auth Information. Username: {}", joinDTO.getUsername());
            return false;
        }

        // 전달받은 joinDTO에서 유효한 값이 입력된 필드만 취급하기 위한 조건문 처리
        if(joinDTO.getUsername() != null) {
            member.get().setUsername(joinDTO.getUsername());
        }
        if(joinDTO.getAge() > 0) { member.get().setAge(joinDTO.getAge()); }
        if(joinDTO.getName() != null) { member.get().setName(joinDTO.getName()); }
        if(joinDTO.getPassword() != null) {
            String encodePassword = bCryptPasswordEncoder.encode(joinDTO.getPassword());
            member.get().setPassword(encodePassword);
        }
        memberRepository.save(member.get());
        logout();
        return true;
    }

    @Transactional
    public boolean deleteMemberById(Long userId) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Member> member = memberRepository.findById(userId);
        if(member.isEmpty()) {
            log.error("Member is Empty. Member id: {}", userId);
            return false;
        }

        // 삭제(탈퇴)를 시도한 유저의 jwt의 유저네임 정보와, 식별자로 찾아낸 유저의 유저네임이 같은 경우에만 해당 회원 삭제를 수행
        if(authUsername.equals(member.get().getUsername())) {
            memberRepository.deleteById(userId);
            logout();
            return true;
        } else {
            log.error("Member id does not match Auth Information. Member id: {}", userId);
            return false;
        }
    }
}
