package com.example.MyBlog.domain.post.service;

import com.example.MyBlog.domain.comment.DTO.ResponseCommentDTO;
import com.example.MyBlog.domain.comment.entity.Comment;
import com.example.MyBlog.domain.image.DTO.ResponseImageDTO;
import com.example.MyBlog.domain.image.entity.Image;
import com.example.MyBlog.domain.image.service.ImageService;
import com.example.MyBlog.domain.likes.entity.Like;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import com.example.MyBlog.domain.post.DTO.RequestAddPostDTO;
import com.example.MyBlog.domain.post.DTO.ResponsePostDTO;
import com.example.MyBlog.domain.post.DTO.ResponsePostListDTO;
import com.example.MyBlog.domain.post.entity.Post;
import com.example.MyBlog.domain.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Autowired
    public PostService(PostRepository postRepository, MemberRepository memberRepository, ImageService imageService) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.imageService = imageService;
    }

    private ResponsePostDTO toDTO(Post post) {
        ResponsePostDTO responsePostDTO = new ResponsePostDTO();
        // 1. 게시글의 기본정보(게시글 식별자, 작성자명) 및 제목과 본문 정보를 변환한다.
        responsePostDTO.setId(post.getId());
        responsePostDTO.setUsername(post.getMemberUsername());
        responsePostDTO.setTitle(post.getTitle());
        responsePostDTO.setContent(post.getContent());
        // 2. 게시글에 포함된 이미지를 찾고 리스트에 추가해서 변환한다.
        if(post.getImages() != null) { // 2-1. 이미지는 없을 수도 있다.
            List<Image> images = post.getImages();
            responsePostDTO.setImages(new ArrayList<>()); // 반환 이미지를 담을 리스트 생성
            for (Image image : images) {
                ResponseImageDTO responseImageDTO = new ResponseImageDTO();
                responseImageDTO.setImageId(image.getId());
                responseImageDTO.setImageUrl(image.getImageUrl());

                responsePostDTO.getImages().add(responseImageDTO);
            }
        }
        // 3. 게시글의 댓글 목록을 조회하여 반환한다.
        if(post.getComments() != null) { // 3-1. 댓글은 없을 수도 있다.
            List<Comment> comments = post.getComments();
            responsePostDTO.setComments(new ArrayList<>()); // 반환 댓글을 담을 리스트 생성
            for (Comment comment : comments) {
                ResponseCommentDTO responseCommentDTO = new ResponseCommentDTO();
                responseCommentDTO.setId(comment.getId());
                responseCommentDTO.setPostId(post.getId());
                responseCommentDTO.setMemberUsername(post.getMember().getUsername());
                responseCommentDTO.setContent(comment.getContent());

                responsePostDTO.getComments().add(responseCommentDTO);
            }
        }
        // 4. '좋아요'를 누른 회원의 유저네임을 조회하여 반환한다.
        if(post.getLikes() != null) { // 4-1. 좋아요는 없을 수도 있다.
            List<Like> likes = post.getLikes();
            responsePostDTO.setLikesMemberUsername(new ArrayList<>()); // 반환 좋아요 목록을 담을 리스트 생성
            for (Like like : likes) {
                responsePostDTO.getLikesMemberUsername().add(like.getMemberUsername());
            }
        }
        return responsePostDTO;
    }

    // 게시글 식별자로 게시글 조회
    @Transactional(readOnly = true)
    public ResponsePostDTO getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            return toDTO(post.get());
        } else {
            return null;
        }
    }

    // 회원 식별자로 해당 회원이 작성한 모든 게시글 정보 조회
    // 게시글의 세부 내용은 클릭 시에만 확인할 수 있으므로, 작성자명과 제목, 작성일자 등의 간단한 정보만 표시
    @Transactional(readOnly = true)
    public List<ResponsePostListDTO> getAllPostsByMemberId(Long memberId) {
        List<Post> posts = postRepository.findAllByMemberId(memberId);
        if(posts.isEmpty()){
            return null;
        }
        List<ResponsePostListDTO> postList = new ArrayList<>();
        for (Post post : posts) {
            ResponsePostListDTO responsePostListDTO = new ResponsePostListDTO();
            responsePostListDTO.setPostId(post.getId());
            responsePostListDTO.setUsername(post.getMemberUsername());
            responsePostListDTO.setPostTitle(post.getTitle());
            postList.add(responsePostListDTO);
        }
        return postList;
    }

    // 게시글 작성 메소드.
    // 게시글 작성이 성공하면 게시글 식별자를 반환한다. 그리고 프론트에서는 이 식별자를 통해 이미지 업로드 api를 요청한다.
    @Transactional
    public Long addPost(RequestAddPostDTO postDTO) {
        Optional<Member> member = memberRepository.findById(postDTO.getMemberId());
        if (member.isEmpty()) {
            return null;
        }
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setMember(member.get());
        Post savedPost = postRepository.save(post);

        return savedPost.getId();
    }


    @Transactional
    public boolean updatePost(Long postId, String title, String content) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 게시글 식별자가 유효하지 않거나 게시글이 존재하지 않다면 수정 불가
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()) {
            return false;
        }

        // 토큰에 담긴 유저정보와 PostDTO에 담긴 유저네임 정보가 일치하지 않으면 수정 불가능
        if(!authUsername.equals(post.get().getMemberUsername())) {
            return false;
        }
        // title 수정
        if(title != null){
            post.get().setTitle(title);
        }
        // content 수정
        if(content != null){
            post.get().setContent(content);
        }
        postRepository.save(post.get());
        return true;
    }

    @Transactional
    public boolean deletePost(Long id) throws IOException {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Post> post = postRepository.findById(id);
        if(post.isEmpty()) {
            // 해당 게시글이 존재하지 않으면 제거 불가
            return false;
        }
        if(!post.get().getMemberUsername().equals(authUsername)) {
            // 해당 게시글의 작성자 유저네임과, 토큰 상의 유저네임이 같지 않으면 제거 불가
            return false;
        }
        // 게시글에 작성된 댓글들은 JPA의 '고아 객체 제거' 옵션으로 자동으로 제거된다.

        postRepository.deleteById(id);
        return true;
    }
}