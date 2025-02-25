package com.example.MyBlog.domain.post.DTO;

import com.example.MyBlog.domain.image.DTO.ResponseImageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResponsePostListDTO {
    private Long postId;
    private String postTitle;
    private String postContent;

    private Long memberId;
    private String username;

    private LocalDateTime createdAt;

    private List<ResponseImageDTO> images; // image url을 반환
    private int likesCount;
    private int commentsCount;
}
