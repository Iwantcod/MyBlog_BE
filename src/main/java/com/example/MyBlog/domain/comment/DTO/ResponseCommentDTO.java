package com.example.MyBlog.domain.comment.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseCommentDTO {
    private Long id;
    private Long postId;
    private Long memberId;
    private String memberUsername;
    private Long parentCommentId;
    private int depth;
    private String content;
    private String createdAt;
}
