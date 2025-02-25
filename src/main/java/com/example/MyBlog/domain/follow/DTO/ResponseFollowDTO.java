package com.example.MyBlog.domain.follow.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseFollowDTO {
    private Long memberId;
    private Long targetId;
}
