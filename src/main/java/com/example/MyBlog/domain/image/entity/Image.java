package com.example.MyBlog.domain.image.entity;

import com.example.MyBlog.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;

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
    private String imageUrl;

    private String locations;

    public void setPost(Post post) {
        this.post = post;
        post.getImages().add(this);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLocation(String locations) {
        this.locations = locations;
    }
}
