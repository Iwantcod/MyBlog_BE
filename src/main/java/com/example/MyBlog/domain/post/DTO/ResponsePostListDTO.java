package com.example.MyBlog.domain.post.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponsePostListDTO {
    private Long postId;
    private String postTitle;
    private String username;
    private LocalDateTime createdAt;
    private int likesCount;
    private int commentsCount;
}
