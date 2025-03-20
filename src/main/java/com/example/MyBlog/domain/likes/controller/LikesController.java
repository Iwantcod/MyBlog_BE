package com.example.MyBlog.domain.likes.controller;

import com.example.MyBlog.domain.likes.DTO.RequestLikesDTO;
import com.example.MyBlog.domain.likes.DTO.ResponseLikesDTO;
import com.example.MyBlog.domain.likes.service.LikesService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/like")
public class LikesController {
    public final LikesService likesService;
    @Autowired
    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }

    @PostMapping // 좋아요 추가: 비동기 처리 필요
    @Operation(summary = "좋아요 처리")
    public ResponseEntity<?> addLike(@RequestBody RequestLikesDTO likeDTO) {
        likesService.addLike(likeDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping // 좋아요 제거: 비동기 처리 필요
    @Operation(summary = "좋아요 제거")
    public ResponseEntity<?> deleteLike(@RequestBody RequestLikesDTO likeDTO) {
        likesService.removeLike(likeDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/{startOffset}") // 특정 게시글의 좋아요 정보 조회
    @Operation(summary = "특정 게시글의 좋아요 정보 조회: 페이징", description = "startOffset은 1부터 시작")
    public ResponseEntity<?> getLikesByPostId(@PathVariable Long postId, @PathVariable Integer startOffset) {
        List<ResponseLikesDTO> likesDTOList = likesService.getLikesByPostId(postId, startOffset);
        if(likesDTOList == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(likesDTOList);
        }
    }

    @GetMapping("/default/{memberId}")
    @Operation(summary = "특정 유저가 좋아요를 누른 모든 게시글의 식별자를 리스트로 반환", description = "로그인 성공 시 수행하여 좋아요한 게시글 표시할 때 사용")
    public ResponseEntity<?> getDefaultLikesInfo(@PathVariable Long memberId) {
        List<Long> postIds = likesService.getDefaultLikesInfo(memberId);
        return ResponseEntity.ok().body(postIds);
    }
}
