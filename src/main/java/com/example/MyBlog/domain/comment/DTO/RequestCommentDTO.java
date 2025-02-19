package com.example.MyBlog.domain.comment.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestCommentDTO {
    private Long postId;
    private Long memberId;
    private String content;
    private Long parentCommentId;
}
