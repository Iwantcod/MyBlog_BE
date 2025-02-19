package com.example.MyBlog.domain.member.controller;

import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.DTO.MemberDTO;
import com.example.MyBlog.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Value("${app.server-url}")
    private String serverUrl;
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    // get user by username
    @GetMapping("/name/{username}")
    public ResponseEntity<MemberDTO> getMemberByUsernameHash(@PathVariable String username) {
        MemberDTO memberDTO = memberService.getMemberByUsername(username);
        if (memberDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(memberDTO, HttpStatus.OK);
        }
    }

    // get user by user id
    @GetMapping("/{userId}")
    public ResponseEntity<MemberDTO> getMemberByUserId(@PathVariable Long userId) {
        MemberDTO memberDTO = memberService.getMemberById(userId);
        if (memberDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(memberDTO, HttpStatus.OK);
        }
    }

    // logout: remove refresh token at redis.
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        if(memberService.logout()) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, serverUrl+"/").build();
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // update member
    @PostMapping("/{userId}")
    public ResponseEntity<?> updateMember(@RequestBody JoinDTO newMemberDTO, @PathVariable Long userId) {
        if(memberService.updateMemberById(newMemberDTO, userId)){
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, serverUrl+"/").build();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // delete member
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long userId) {

        if(memberService.deleteMemberById(userId)) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, serverUrl+"/").build();
        } else {
            return ResponseEntity.status(404).build();
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
