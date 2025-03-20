package com.example.MyBlog.domain.member.controller;

import com.example.MyBlog.domain.follow.service.FollowService;
import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.DTO.MemberDTO;
import com.example.MyBlog.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final FollowService followService;
    @Value("${app.client-url}")
    private String clientUrl;
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService, FollowService followService) {
        this.memberService = memberService;
        this.followService = followService;
    }


    // get user by username
    @GetMapping("/name/{username}")
    @Operation(summary = "회원 유저네임으로 조회")
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
    @Operation(summary = "회원 식별자로 조회")
    public ResponseEntity<MemberDTO> getMemberByUserId(@PathVariable Long userId) {
        MemberDTO memberDTO = memberService.getMemberById(userId);

        if (memberDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            // 팔로워 및 팔로잉 수 조회(카운팅)해서 반환
            int[] followCountInfo = followService.getFollowCountInfo(userId);
            memberDTO.setFollowerCount(followCountInfo[0]);
            memberDTO.setFollowingCount(followCountInfo[1]);
            return new ResponseEntity<>(memberDTO, HttpStatus.OK);
        }
    }

    // logout: remove refresh token at redis.
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "해당 회원의 Refresh Token을 Redis에서 제거")
    public ResponseEntity<?> logout() {
        if(memberService.logout()) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, clientUrl+"/auth/login").build();
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // update member
    @PatchMapping("/{userId}")
    @Operation(summary = "회원 정보 수정", description = "자기 자신이 아니면 수정 불가(jwt로 검증)")
    public ResponseEntity<?> updateMember(@RequestBody JoinDTO newMemberDTO, @PathVariable Long userId) {
        if(memberService.updateMemberById(newMemberDTO, userId)){
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, clientUrl).build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    // delete member
    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴", description = "자기 자신이 아니면 탈퇴 불가(jwt로 검증)")
    public ResponseEntity<?> deleteMember(@PathVariable Long userId) {
        followService.deleteFollowByMemberId(userId); // 삭제할 회원과 관련된 모든 팔로우정보 제거
        if(memberService.deleteMemberById(userId)) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, clientUrl+"/auth/login").build();
        } else {
            return ResponseEntity.status(401).build();
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
