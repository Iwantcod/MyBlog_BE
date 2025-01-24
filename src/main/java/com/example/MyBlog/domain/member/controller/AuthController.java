package com.example.MyBlog.domain.member.controller;

import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.DTO.MemberDTO;
import com.example.MyBlog.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final MemberService memberService;
    @Autowired
    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDTO joinDTO) {
        MemberDTO memberDTO = memberService.join(joinDTO);
        if (memberDTO != null) {
            return ResponseEntity.ok(memberDTO);
        } else {
            return ResponseEntity.status(401).body("Cannot Join Member");
        }
    }
}
