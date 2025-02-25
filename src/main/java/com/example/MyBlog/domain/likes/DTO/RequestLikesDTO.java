package com.example.MyBlog.domain.likes.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RequestLikesDTO {
    private Long id;
    private Long postId;
    private Long memberId;
}
