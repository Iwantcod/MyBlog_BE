package com.example.MyBlog.domain.comment.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseCommentDTO {
    private Long id;
    private Long postId;
    private String content;
    private String memberUsername;
}
