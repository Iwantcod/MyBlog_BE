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
    // 날짜 필드 json 직렬화 포멧 지정
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
