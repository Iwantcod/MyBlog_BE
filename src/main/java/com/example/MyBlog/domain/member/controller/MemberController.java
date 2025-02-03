package com.example.MyBlog.domain.member.controller;

import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.DTO.MemberDTO;
import com.example.MyBlog.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        MemberDTO memberDTO = memberService.getMemberById(id);
        if(memberDTO == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(memberDTO, HttpStatus.OK);
        }
    }

    // 문자열인 유저네임을 입력값으로 받아, 그것을 해시값으로 변환한 값으로 데이터베이스에서 검색
    // jwt를 이용한 인증에서 유저네임을 기준으로 조회할 때 사용한다.
    @GetMapping("/{username}")
    public ResponseEntity<MemberDTO> getMemberByUsernameHash(@PathVariable String username) {
        MemberDTO memberDTO = memberService.getMemberByUsernameHash(username);
        if(memberDTO == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(memberDTO, HttpStatus.OK);
        }
    }

    @PatchMapping("/")
    public ResponseEntity<MemberDTO> updateMember(@RequestBody JoinDTO newMemberDTO) {
        MemberDTO memberDTO = memberService.updateMember(newMemberDTO);
        if(memberDTO == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(memberDTO, HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        if(memberService.deleteMemberById(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


//    @GetMapping("/session-info") // SecurityContextHolder의 임시 세션에 대한 정보 확인
//    public ResponseEntity<String> sessionInfo() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority grantedAuthority = iterator.next();
//        String role = grantedAuthority.getAuthority();
//
//        String result = username + " " + role;
//        System.out.println(result);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

}
