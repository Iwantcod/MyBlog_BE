package com.example.MyBlog.domain.likes.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseLikesDTO {
    private Long id;
    private Long memberId;
    private Long postId;
}
