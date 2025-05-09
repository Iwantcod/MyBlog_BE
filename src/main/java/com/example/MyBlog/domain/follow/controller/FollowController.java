package com.example.MyBlog.domain.follow.controller;

import com.example.MyBlog.domain.follow.DTO.RequestFollowDTO;
import com.example.MyBlog.domain.follow.DTO.ResponseFollowDTO;
import com.example.MyBlog.domain.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;
    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    @Operation(summary = "팔로우")
    public ResponseEntity<?> follow(@RequestBody RequestFollowDTO requestFollowDTO) {
        if(followService.follow(requestFollowDTO)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping
    @Operation(summary = "언팔로우")
    public ResponseEntity<?> unfollow(@RequestBody RequestFollowDTO requestFollowDTO) {
        if(followService.unfollow(requestFollowDTO)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/target/{memberId}") // 특정 유저의 팔로우 목록 조회
    @Operation(summary = "특정 유저가 팔로우한 회원 목록을 조회")
    public ResponseEntity<?> getTargets(@PathVariable Long memberId) {
        List<ResponseFollowDTO> responseFollowDTOList = followService.getTargets(memberId);
        if(responseFollowDTOList == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok().body(responseFollowDTOList);
        }
    }

    @GetMapping("/follower/{targetId}") // 특정 유저의 팔로워 목록 조회
    @Operation(summary = "특정 유저를 팔로우하는 회원 목록을 조회")
    public ResponseEntity<?> getFollowers(@PathVariable Long targetId) {
        List<ResponseFollowDTO> responseFollowDTOList = followService.getFollowers(targetId);
        if(responseFollowDTOList == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(responseFollowDTOList);
    }
}
