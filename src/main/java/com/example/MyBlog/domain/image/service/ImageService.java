package com.example.MyBlog.domain.image.service;

import com.example.MyBlog.domain.image.DTO.ResponseImageDTO;
import com.example.MyBlog.domain.image.entity.Image;
import com.example.MyBlog.domain.image.repository.ImageRepository;
import com.example.MyBlog.domain.post.entity.Post;
import com.example.MyBlog.domain.post.repository.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.storage-path}")
    private String storagePath;

    @Autowired
    public ImageService(ImageRepository imageRepository, PostRepository postRepository) {
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
    }

    private ResponseImageDTO toDto(Image image) {
        ResponseImageDTO responseImageDTO = new ResponseImageDTO();
        responseImageDTO.setImageUrl(image.getImageUrl());
        responseImageDTO.setImageId(image.getId());
        return responseImageDTO;
    }

    // 이미지 저장(이미 생성된 게시글에 이미지를 추가할 시 사용)
    @Transactional
    public boolean addImages(List<MultipartFile> fileList, Long postId) throws IOException {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()) {
            log.error("ADD images FAIL: post empty");
            return false;
        }

        // 스토리지 기본 경로에 해당하는 디렉토리가 존재하지 않는 경우, 디렉토리(스토리지) 생성: 에러 방지
        Files.createDirectories(Paths.get(storagePath));

        for (MultipartFile file : fileList) {
            String originalFilename = file.getOriginalFilename();
            if(originalFilename == null) {
                throw new IllegalArgumentException("Invalid file name");
            }

            // 파일 확장자 추출
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 새로운 파일명 생성: uuid + 확장자
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            // 파일 저장 경로 생성
            String filePath = storagePath + newFileName;

            Thumbnails.of(file.getInputStream())  // 업로드된 파일의 입력 스트림 사용
                    .size(800, 600)               // 이미지 크기 조절 (800x600 픽셀)
                    .outputQuality(0.7)           // 품질 조절 (70% 품질로 압축)
                    .toFile(filePath);            // 최종적으로 압축된 파일 저장

            // 이미지 테이블에 저장
            Image image = new Image();
            image.setImageUrl(newFileName);
            image.setPost(post.get());
            imageRepository.save(image);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public List<ResponseImageDTO> getImageByPost(Long postId) {
        List<Image> images = imageRepository.findAllByPostId(postId);
        // 해당 게시글에 아무런 이미지가 없다면 null
        if(images.isEmpty()){
            return null;
        }

        // 있으면 리스트로 반환
        List<ResponseImageDTO> result = new ArrayList<>();
        for (Image image : images) {
            result.add(toDto(image));
        }
        return result;
    }

    public byte[] getImageFile(String imagename) throws IOException {
        File imageFile = new File(storagePath + imagename);
        if(!imageFile.exists()) {
            log.error("Image file does not exist");
            return null;
        }
        FileInputStream fis = new FileInputStream(imageFile);
        byte[] imageBytes = IOUtils.toByteArray(fis);
        return imageBytes;
    }


    @Transactional // 이미지 식별자로 이미지 제거
    public boolean deleteImageById(List<Long> imageIds) throws IOException {

        for (Long imageId : imageIds) {
            // 1. db에서 이미지 저장 위치정보 조회
            Optional<Image> image = imageRepository.findById(imageId);
            if(image.isEmpty()) {
                return false;
            }

            // 2. 스토리지에서 해당 경로에 해당하는 이미지 파일 삭제
            String filePath = storagePath + File.separator + image.get().getImageUrl();
            File file = new File(filePath);
            if(file.exists()){
                // 파일 제거 후 제거 여부를 bool 값으로 반환받음.
                boolean deleted = file.delete();
                if(!deleted){
                    // 제거 실패 시 false 반환
                    return false;
                }
            }

            // 3. db에서 이미지 정보 삭제
            imageRepository.deleteById(imageId);
        }
        return true;
    }

    @Transactional // 게시글 식별자로 게시글에 소속된 이미지 모두 제거
    public boolean deleteImageByPostId(Long postId) throws IOException {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()) {
            return false;
        }
        if (!post.get().getMemberUsername().equals(authUsername)) {
            log.error("DELETE Post FAIL: Authorization information mismatch. post id: {}", postId);
            return false;
        }
        // 1. db에서 이미지 저장 위치정보 조회
        List<Image> images = imageRepository.findAllByPost(post.get());
        if(images.isEmpty()){
            return true;
        }

        // 2. 스토리지에서 해당 경로에 해당하는 이미지 파일 삭제
        for (Image image : images) {
            String filePath = storagePath + File.separator + image.getImageUrl();
            File file = new File(filePath);
            if(file.exists()){
                // 파일 제거 후 제거 여부를 bool 값으로 반환받음.
                boolean deleted = file.delete();
                if(!deleted){
                    // 제거 실패 시 false 반환
                    return false;
                }
            }
        }

        // 3. db에서 특정 게시글의 이미지 정보 일괄 삭제
        imageRepository.deleteAllByPostId(postId);
        entityManager.flush(); // Persistence Context에 pending상태로 저장된 변경사항이 실제 DB에 전송되어 반영
        entityManager.clear(); // 엔티티 매니저가 관리하는 모든 엔티티 인스턴스를 제거 -> DB와 영속성 컨텍스트 사이의 불일치 문제 해결(방지)
        return true;
    }


    // 이미지는 '수정'하는 개념이 아니므로, update는 구현하지 않는다.
}
