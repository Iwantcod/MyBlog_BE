package com.example.MyBlog.domain.post.controller;

import com.example.MyBlog.domain.image.service.ImageService;
import com.example.MyBlog.domain.post.DTO.RequestAddPostDTO;
import com.example.MyBlog.domain.post.DTO.RequestUpdatePostDTO;
import com.example.MyBlog.domain.post.DTO.ResponsePostDTO;
import com.example.MyBlog.domain.post.DTO.ResponsePostListDTO;
import com.example.MyBlog.domain.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    public final PostService postService;
    private final ImageService imageService;

    @Autowired
    public PostController(PostService postService, ImageService imageService) {
        this.postService = postService;
        this.imageService = imageService;
    }


    // multipart/form-data 요청을 받기 위해 consumes 옵션 설정
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPost(@ModelAttribute RequestAddPostDTO postDTO) {
        // 1. postDTO 내용을 db에 삽입
        Long postResult = postService.addPost(postDTO); // 삽입 후 게시글 식별자를 반환(Long)
        // 2. 삽입 후 생성된 게시글 엔티티의 식별자를 이용해서 image 업로드
        if(postResult != null) {
            if(postDTO.getImageList() != null) {
                try {
                    // imageService.addImage는 업로드 성공 여부를 반환(bool)
                    if(!imageService.addImages(postDTO.getImageList(), postResult)) {
                        return ResponseEntity.badRequest().body("Failed to upload image");
                    }
                } catch (IOException e){
                    return ResponseEntity.internalServerError().body("Server Error to upload image");
                }
            }
            // 3. 업로드 완료
            return ResponseEntity.ok().body("Success to add post");
        } else {
            return ResponseEntity.badRequest().body("Failed to add post");
        }
    }

    @GetMapping("/{postId}") // post 식별자로 게시글 조회
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        ResponsePostDTO responsePost = postService.getPostById(postId);
        if(responsePost != null) {
            return ResponseEntity.ok().body(responsePost);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/member/{memberId}") // 특정 회원이 작성한 게시글 모두 조회
    public ResponseEntity<?> getPostByMemberId(@PathVariable Long memberId) {
        List<ResponsePostListDTO> responsePostList = postService.getAllPostsByMemberId(memberId);
        if(responsePostList != null) {
            return ResponseEntity.ok().body(responsePostList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PatchMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(@ModelAttribute RequestUpdatePostDTO postDTO) {
        //1. 프론트로부터 넘겨받은 새로운 ‘제목’, ‘본문’ 정보를 post 테이블에 업데이트한다. 이 업데이트 성공 시 이후 내용을 수행
        if(postService.updatePost(postDTO.getPostId(), postDTO.getTitle(), postDTO.getContent())) {
            // 게시글에서 제거할 이미지가 있는 경우 이미지 제거작업
            if(postDTO.getDeletedImageIdList() != null) {
                //2. 제거되는 이미지들의 식별자를 리스트로 넘겨받고, imageService를 이용하여 스토리지에서 이미지 제거하고 image 테이블에서 제거한다.
                // IOException 예외처리
                try {
                    if(!imageService.deleteImageById(postDTO.getDeletedImageIdList())) {
                        // 제거에 실패할 경우 다음을 반환
                        return ResponseEntity.badRequest().body("Failed to delete image - update");
                    }
                } catch (IOException e) {
                    // 예외처리
                    return ResponseEntity.internalServerError().body("Server Error to delete image - update");
                }
            }

            // 게시글에 새롭게 추가할 이미지가 있는 경우 이미지 추가작업
            if(postDTO.getNewImageList() != null) {
                //3. 새롭게 추가되는 이미지를 리스트를 통해 넘겨받고, imageService를 이용하여 스토리지에 저장 및 image 테이블에 추가(저장 경로)한다.
                // IOException 예외처리
                try{
                    if(!imageService.addImages(postDTO.getNewImageList(), postDTO.getPostId())) {
                        // 추가에 실패할 경우 다음을 반환
                        return ResponseEntity.badRequest().body("Failed to upload image - update");
                    }
                } catch (IOException e) {
                    // 예외처리
                    return ResponseEntity.internalServerError().body("Server Error to upload image - update");
                }
            }
            //4. 위 3가지 단계를 모두 성공적으로 마쳤으면 게시글 수정 성공
            return ResponseEntity.ok().body("Success to update post");
        } else {
            return ResponseEntity.badRequest().body("Failed to update post");
        }
    }


    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            if(postService.deletePost(postId) && imageService.deleteImageByPostId(postId)) {
                // 이미지도 모두 삭제
                return ResponseEntity.ok().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Server Error to delete post");
        }
        return ResponseEntity.badRequest().body("Delete post failed");
    }
}
