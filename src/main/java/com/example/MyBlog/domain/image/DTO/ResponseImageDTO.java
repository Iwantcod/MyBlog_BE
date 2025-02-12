package com.example.MyBlog.domain.image.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseImageDTO {
    private Long imageId;
    private String imageUrl;
}
