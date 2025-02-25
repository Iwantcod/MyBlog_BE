package com.example.MyBlog.domain.likes.controller;

import com.example.MyBlog.domain.likes.DTO.RequestLikesDTO;
import com.example.MyBlog.domain.likes.DTO.ResponseLikesDTO;
import com.example.MyBlog.domain.likes.service.LikesService;
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
    public ResponseEntity<?> addLike(@RequestBody RequestLikesDTO likeDTO) {
        likesService.addLike(likeDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping // 좋아요 제거: 비동기 처리 필요
    public ResponseEntity<?> deleteLike(@RequestBody RequestLikesDTO likeDTO) {
        likesService.removeLike(likeDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/{startOffset}") // 특정 게시글의 좋아요 정보 조회
    public ResponseEntity<?> getLikesByPostId(@PathVariable Long postId, @PathVariable Integer startOffset) {
        List<ResponseLikesDTO> likesDTOList = likesService.getLikesByPostId(postId, startOffset);
        if(likesDTOList == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(likesDTOList);
        }
    }

    @GetMapping("/default/{memberId}")
    public ResponseEntity<?> getDefaultLikesInfo(@PathVariable Long memberId) {
        List<Long> postIds = likesService.getDefaultLikesInfo(memberId);
        return ResponseEntity.ok().body(postIds);
    }
}
