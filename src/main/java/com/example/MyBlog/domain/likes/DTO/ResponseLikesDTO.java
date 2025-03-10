package com.example.MyBlog.domain.likes.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseLikesDTO {
    private Long id;
    private Long memberId;
    private String memberUsername;
    private Long postId;
    private LocalDateTime createdAt;
}
