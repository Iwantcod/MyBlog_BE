package com.example.MyBlog.domain.post.controller;

import com.example.MyBlog.domain.image.DTO.ResponseImageDTO;
import com.example.MyBlog.domain.image.service.ImageService;
import com.example.MyBlog.domain.likes.service.LikesService;
import com.example.MyBlog.domain.post.DTO.RequestAddPostDTO;
import com.example.MyBlog.domain.post.DTO.RequestUpdatePostDTO;
import com.example.MyBlog.domain.post.DTO.ResponsePostDTO;
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
    private final LikesService likesService;

    @Autowired
    public PostController(PostService postService, ImageService imageService, LikesService likesService) {
        this.postService = postService;
        this.imageService = imageService;
        this.likesService = likesService;
    }


    // create post
    // multipart/form-data 요청을 받기 위해 consumes 옵션 설정
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPost(@ModelAttribute RequestAddPostDTO postDTO) {
        // 1. postDTO 내용을 db에 삽입
        Long postResult = postService.addPost(postDTO); // 삽입 후 게시글 식별자를 반환(Long)
        // 2. 삽입 후 생성된 게시글 엔티티의 식별자를 이용해서 image 업로드
        if(postResult != null) {
            if(postDTO.getImageList() != null) {
                try {
                    // 이미지 파일은 게시글 당 최대 5개까지만 업로드 가능
                    if(postDTO.getImageList().size() > 5) {
                        return ResponseEntity.badRequest().body("Cannot upload more than 5 images");
                    } else if(!imageService.addImages(postDTO.getImageList(), postResult)) {
                        // imageService.addImage는 업로드 성공 여부를 반환(bool)
                        return ResponseEntity.badRequest().body("Failed to upload image");
                    }
                } catch (IOException e){
                    return ResponseEntity.internalServerError().body("Server Error to upload image");
                }
            }
            // 3. 업로드 완료
            return ResponseEntity.status(201).body("Success to add post");
        } else {
            return ResponseEntity.badRequest().body("Failed to add post");
        }
    }

    // 게시글 식별자 목록을 받아서 조회
    // 활용 1: 특정 유저가 좋아요 누른 게시글 정보 조회(PostController)
        // 1. 로그인 성공 시 해당 회원이 좋아요 누른 모든 게시글의 식별자를 클라이언트로 반환, 클라이언트는 이를 보관
        // 2. 클라이언트에서 보관한 게시글 식별자 배열에서 10개씩 서버로 조회 요청
        // 3. PostService: 조회 결과에서 게시글 식별자를 뽑아내서 게시글 10개 조회(10번의 쿼리 발생)
        // 4. PostController: 조회된 게시글 10개의 정보를 클라이언트로 반환
    @GetMapping // 예시: GET /api/post?targets=1,2,3
    public ResponseEntity<?> getPost(@RequestParam List<Long> targets) {
        List<ResponsePostDTO> responsePostList = postService.getPostById(targets);

        for (ResponsePostDTO responsePostDTO : responsePostList) {
            if(responsePostDTO.getImagesCount() > 0) {
                List<ResponseImageDTO> responseImage = imageService.getImageByPost(responsePostDTO.getId());
                if(responseImage != null) {
                    responsePostDTO.setImages(responseImage); // 반환값에 이미지 주소 리스트 추가
                }
            }
        }

        if(!responsePostList.isEmpty()) {
            return ResponseEntity.ok().body(responsePostList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // get 10 posts by member id paging
    @GetMapping("/own/{memberId}/{startOffset}") // 특정 회원이 작성한 게시글 모두 조회
    public ResponseEntity<?> getPostByMemberId(@PathVariable Long memberId, @PathVariable Integer startOffset) {
        List<ResponsePostDTO> responsePostList = postService.getAllPostsByMemberId(memberId, startOffset);
        if(responsePostList != null) {
            for (ResponsePostDTO responsePostDTO : responsePostList) {
                if(responsePostDTO.getImagesCount() > 0) { // 게시글의 이미지 카운트가 0보다 크다면 해당 게시글에 이미지 추가해서 반환
                    List<ResponseImageDTO> responseImage = imageService.getImageByPost(responsePostDTO.getId());
                    if(responseImage != null) {
                        responsePostDTO.setImages(responseImage);
                    }
                }
            }
            return ResponseEntity.ok().body(responsePostList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // get 10 posts paging
    @GetMapping("/recent/{startOffset}") // 최신순 게시글 10개 조회(페이징)
    public ResponseEntity<?> getPostListPaging(@PathVariable Integer startOffset) {
        List<ResponsePostDTO> responsePostList = postService.getPostListPaging(startOffset);
        if(responsePostList != null) {
            for (ResponsePostDTO responsePostDTO : responsePostList) {
                if(responsePostDTO.getImagesCount() > 0) { // 게시글의 이미지 카운트가 0보다 크다면 해당 게시글에 이미지 추가해서 반환
                    List<ResponseImageDTO> responseImage = imageService.getImageByPost(responsePostDTO.getId());
                    if(responseImage != null) {
                        responsePostDTO.setImages(responseImage);
                    }
                }
            }
            return ResponseEntity.ok().body(responsePostList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    // Multipart 전송을 위해 PATCH가 아닌 POST 혹은 PUT을 사용해야 하는데, POST 선택'
    // Multipart 데이터 전송은 POST에 최적화되어있다.
    @PostMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(@ModelAttribute RequestUpdatePostDTO postDTO, @PathVariable Long postId) {
        //1. 프론트로부터 넘겨받은 새로운 ‘제목’, ‘본문’ 정보를 post 테이블에 업데이트한다. 이 업데이트 성공 시 이후 내용을 수행
        if(postService.updatePost(postId, postDTO.getTitle(), postDTO.getContent())) {
            // 게시글에서 제거할 이미지가 있는 경우 이미지 제거작업
            if(postDTO.getDeletedImageIdList() != null) {
                //2. 제거되는 이미지들의 식별자를 리스트로 넘겨받고, imageService를 이용하여 스토리지에서 이미지 제거하고 image 테이블에서 제거한다.
                // IOException 예외처리
                try {
                    if(!imageService.deleteImageById(postDTO.getDeletedImageIdList(), postId)) {
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
                    // 이미지 파일은 게시글 당 최대 5개까지만 업로드 가능
                    if(imageService.getImageByPost(postId).size() > 5) {
                        return ResponseEntity.badRequest().body("Cannot upload more than 5 images");
                    } else if(!imageService.addImages(postDTO.getNewImageList(), postId)) {
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


    // delete post by post id
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            // 이미지 먼저 삭제
            if(imageService.deleteImageByPostId(postId)) {

                if(postService.deletePost(postId)) {
                    return ResponseEntity.ok().build();
                }
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Server Error to delete post");
        }
        return ResponseEntity.badRequest().body("Delete post failed");
    }
}
