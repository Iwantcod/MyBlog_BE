package com.example.MyBlog.domain.post.DTO;

import com.example.MyBlog.domain.comment.DTO.ResponseCommentDTO;
import com.example.MyBlog.domain.image.DTO.ResponseImageDTO;
import com.example.MyBlog.domain.likes.DTO.ResponseLikesDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResponsePostDTO {
    private Long id;
    private Long memberId;
    private String title;
    private String username; // 작성자 유저네임
    private LocalDateTime createdAt;

    private List<ResponseImageDTO> images; // image url을 반환
    private int likesCount;
    private int commentsCount;
    private int imagesCount;
}
