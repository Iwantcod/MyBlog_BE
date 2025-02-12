package com.example.MyBlog.domain.post.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RequestAddPostDTO {
    private Long memberId;
//    private String memberUsername;
    private String title;
    private String content;
    private List<MultipartFile> imageList;
}
