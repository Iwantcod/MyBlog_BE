package com.example.MyBlog.domain.member.controller;

import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final MemberService memberService;

    @Value("${app.server-url}")
    private String serverUrl;

    @Autowired
    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDTO joinDTO) {
        if (memberService.join(joinDTO)) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, serverUrl + "/").build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        if(memberService.isDuplicationUsername(username)) {
            return ResponseEntity.status(409).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}
