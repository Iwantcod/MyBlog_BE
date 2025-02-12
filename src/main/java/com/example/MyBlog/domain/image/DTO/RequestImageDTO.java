package com.example.MyBlog.domain.image.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RequestImageDTO {
    private Long postId;
    private List<MultipartFile> fileList;
}
