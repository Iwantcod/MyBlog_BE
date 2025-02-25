package com.example.MyBlog.domain.follow.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RequestFollowDTO {
    private Long memberId;
    private Long targetId;
}
