package com.example.MyBlog.domain.image.entity;

import com.example.MyBlog.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Table(name = "IMAGES")
public class Image {
    @Id @GeneratedValue @Column(name = "IMAGE_ID")
    private Long id;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(nullable = false)
    private String imageUrl; // 이미지 저장 경로

//    @Column(nullable = false)
//    private int imageSeq; // 이미지 저장 순서(화면에 표시할 때 사용)

//    @Column(nullable = false)
//    @ColumnDefault("false")
//    private boolean isDeleted;


    public void setPost(Post post) {
        this.post = post;
        post.getImages().add(this);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
