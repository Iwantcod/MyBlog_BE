package com.example.MyBlog.domain.comment.controller;

import com.example.MyBlog.domain.comment.DTO.RequestCommentDTO;
import com.example.MyBlog.domain.comment.DTO.ResponseCommentDTO;
import com.example.MyBlog.domain.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{postId}/{startOffset}")
    public ResponseEntity<?> getCommentByPostIdPaging(@PathVariable Long postId, @PathVariable Integer startOffset) {
        List<ResponseCommentDTO> commentList = commentService.getCommentsByPostIdPaging(postId, startOffset);
        if (commentList.isEmpty()) {
            return ResponseEntity.status(404).body("Cannot Found Comments");
        } else {
            return ResponseEntity.ok(commentList);
        }
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody RequestCommentDTO requestCommentDTO) {
        if(commentService.addComment(requestCommentDTO)) {
            return ResponseEntity.status(201).body("Comment Created");
        } else {
            return ResponseEntity.status(400).body("Comment Not Created");
        }
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@RequestBody String updatedContent, @PathVariable Long commentId) {
        if(commentService.updateComment(updatedContent, commentId)) {
            return ResponseEntity.status(200).body("Comment Updated");
        } else {
            return ResponseEntity.status(400).body("Comment Not Updated");
        }
    }

    @PatchMapping("/off/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        if(commentService.deleteComment(commentId)) {
            return ResponseEntity.status(200).body("Comment Deleted");
        } else {
            return ResponseEntity.status(400).body("Comment Not Deleted");
        }
    }


}
