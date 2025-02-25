package com.example.MyBlog.domain.image.controller;

import com.example.MyBlog.domain.image.DTO.RequestImageDTO;
import com.example.MyBlog.domain.image.DTO.ResponseImageDTO;
import com.example.MyBlog.domain.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    private final ImageService imageService;


    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    // 이미지를 전송받을 때에는 Json 방식을 이용하지 않는다.
    // 쿼리 문자열이 get 방식과는 달리, 메시지의 body로 전송받는다. (ModelAttribute)
    @PostMapping
    public ResponseEntity<?> uploadImages(@ModelAttribute RequestImageDTO imageDTO) {
        List<MultipartFile> fileList = imageDTO.getFileList();
        Long postId = imageDTO.getPostId();

        if(fileList.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty image");
        }

        try {
            if(imageService.addImages(fileList, postId)) {
                return ResponseEntity.ok().body("Success to upload image");
            } else {
                return ResponseEntity.badRequest().body("Failed to upload image");
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Server Error to upload image"); // 500 메시지
        }
    }

    @GetMapping(value = "/file/{imagename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getImageFile(@PathVariable String imagename) {
        try {
            // 이미지 이름을 통해 실제로 불러온 이미지 파일(바이트)
            byte[] imageBytes = imageService.getImageFile(imagename);
            if(imageBytes == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error! Cannot Serve Image File.");
        }
    }


    @GetMapping("/{postId}") // postId를 통한 이미지 경로 조회
    public ResponseEntity<?> getImagePath(@PathVariable Long postId) {
        List<ResponseImageDTO> imageList = imageService.getImageByPost(postId);
        if(imageList.isEmpty()) {
            return ResponseEntity.badRequest().body("No image found");
        } else {
            return ResponseEntity.ok().body(imageList);
        }

    }

    @DeleteMapping // List<Long>를 통한 복수의 이미지 삭제
    public ResponseEntity<?> deleteImage(@RequestBody List<Long> imageIds, @PathVariable Long postId) {
        try {
            if(imageService.deleteImageById(imageIds, postId)) {
                return ResponseEntity.ok().body("Success to delete image");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete image");
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Server Error to delete image by id");
        }
    }

    @DeleteMapping("/{postId}") // 게시글 하나에 소속되어있는 모든 이미지 삭제
    public ResponseEntity<?> deleteAllImageByPost(@PathVariable Long postId) {
        try {
            if(imageService.deleteImageByPostId(postId)) {
                return ResponseEntity.ok().body("Success to delete all images by post");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete all images by post");
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Server Error to delete images by post");
        }
    }


    // 이미지는 그 자체를 '수정'할 일이 없다. 따라서 구현하지 않는다.
}
