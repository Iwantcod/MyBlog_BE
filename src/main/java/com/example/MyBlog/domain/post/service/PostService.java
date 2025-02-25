package com.example.MyBlog.domain.post.service;

import com.example.MyBlog.domain.likes.DTO.ResponseLikesDTO;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import com.example.MyBlog.domain.post.DTO.RequestAddPostDTO;
import com.example.MyBlog.domain.post.DTO.ResponsePostDTO;
import com.example.MyBlog.domain.post.entity.Post;
import com.example.MyBlog.domain.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    @Autowired
    public PostService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private ResponsePostDTO toDTO(Post post) {
        ResponsePostDTO responsePostDTO = new ResponsePostDTO();
        // 게시글의 기본정보(게시글 식별자, 작성자명, 좋아요 수) 및 제목과 본문 정보를 변환한다.
        responsePostDTO.setId(post.getId());
        responsePostDTO.setMemberId(post.getMember().getId()); // 작성자 회원 식별자
        responsePostDTO.setUsername(post.getMemberUsername());
        responsePostDTO.setTitle(post.getTitle());
        responsePostDTO.setLikesCount(post.getLikesCount()); // 좋아요 누른 유저정보는 LAZY하게 조회
        responsePostDTO.setCommentsCount(post.getCommentsCount());
        responsePostDTO.setCreatedAt(post.getCreatedAt());
        responsePostDTO.setImagesCount(post.getImagesCount());
        return responsePostDTO;
    }

    // 게시글 식별자로 게시글 조회
    @Transactional(readOnly = true)
    public List<ResponsePostDTO> getPostById(List<Long> postIds) {
        List<ResponsePostDTO> responsePostDTOList = new ArrayList<>();
        for (Long postId : postIds) {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isPresent()) {
                responsePostDTOList.add(toDTO(post.get()));
            }
        }

        if (responsePostDTOList != null) {
            return responsePostDTOList;
        } else {
            log.error("GET Post FAIL: Cannot find Posts.");
            return null;
        }
    }



    // 회원 식별자로 해당 회원이 작성한 모든 게시글 정보 조회
    // 게시글의 세부 내용은 클릭 시에만 확인할 수 있으므로, 작성자명과 제목, 작성일자 등의 간단한 정보만 표시
    @Transactional(readOnly = true)
    public List<ResponsePostDTO> getAllPostsByMemberId(Long memberId, Integer startOffset) {
        // 프론트로부터 1,2,3,4... 의 값을 받는다. 이 값에 10을 곱한 값이 조회 시작지점이다.
        int pageSize = 10;

        // 프론트에서는 (1 ~ )의 값을 받지만, 데이터베이스에 쿼리문을 날릴땐 당연히 0부터 시작한다.
        int startPage = startOffset - 1;
        Pageable pageable = PageRequest.of(startPage, pageSize, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findAllByMemberId(memberId, pageable);
        if(posts.isEmpty()){
            log.error("GET Post By Member FAIL: This member doesn't have any posts. member id: {}", memberId);
            return null;
        }

        Page<ResponsePostDTO> dtoPage = posts.map(this::toDTO);
        log.info("GET Post SUCCESS By Member ID: {}.", memberId);
        return dtoPage.getContent();
    }


    @Transactional(readOnly = true) // 최신순 게시글 조회
    public List<ResponsePostDTO> getPostListPaging(Integer startOffset) {
        // 프론트로부터 1,2,3,4... 의 값을 받는다. 이 값에 10을 곱한 값이 조회 시작지점이다.
        int pageSize = 10;

        // 프론트에서는 (1 ~ )의 값을 받지만, 데이터베이스에 쿼리문을 날릴땐 당연히 0부터 시작한다.
        int startPage = startOffset - 1;
        Pageable pageable = PageRequest.of(startPage, pageSize, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findAll(pageable);
        if(posts.isEmpty()){
            log.error("GET Post FAIL: Cannot find Post.");
            return null;
        }

        Page<ResponsePostDTO> dtoPage = posts.map(this::toDTO);
        log.info("GET Post SUCCESS.");
        return dtoPage.getContent(); // Page 객체의 메타데이터 없이 본문 데이터만 반환
    }

    // 게시글 작성 메소드.
    // 게시글 작성이 성공하면 게시글 식별자를 반환한다. 그리고 프론트에서는 이 식별자를 통해 이미지 업로드 api를 요청한다.
    @Transactional
    public Long addPost(RequestAddPostDTO postDTO) {
        Optional<Member> member = memberRepository.findById(postDTO.getMemberId());
        if (member.isEmpty()) {
            log.error("Create Post FAIL: Authorization information mismatch. member id: {}", postDTO.getMemberId());
            return null;
        }
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setMember(member.get());
        Post savedPost = postRepository.save(post);

        log.info("Create Post SUCCESS. post id: {}", savedPost.getId());
        return savedPost.getId();
    }


    @Transactional
    public boolean updatePost(Long postId, String title, String content) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 게시글 식별자가 유효하지 않거나 게시글이 존재하지 않다면 수정 불가
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()) {
            log.error("UPDATE Post FAIL: Post Empty. post id: {}", postId);
            return false;
        }

        // 토큰에 담긴 유저정보와 인자로 받은 회원 식별자로 조회한 유저네임 정보가 일치하지 않으면 수정 불가능
        if(!authUsername.equals(post.get().getMember().getUsername())) {
            log.error("UPDATE Post FAIL: Authorization information mismatch. post id: {}", postId);
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
        log.info("UPDATE Post SUCCESS. Post id: {}", postId);
        return true;
    }

    @Transactional
    public boolean deletePost(Long id) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Post> post = postRepository.findById(id);
        if(post.isEmpty()) {
            // 해당 게시글이 존재하지 않으면 제거 불가
            log.error("DELETE Post FAIL: Post Empty. id: {}", id);
            return false;
        }
        if(!authUsername.equals(post.get().getMember().getUsername())) {
            // 해당 게시글의 작성자의 식별자를 통해 조회한 유저네임과, 토큰 상의 유저네임이 같지 않으면 제거 불가
            log.error("DELETE Post FAIL: Authorization information mismatch. post id: {}", id);
            return false;
        }
        // 게시글에 작성된 댓글들은 JPA의 '고아 객체 제거' 옵션으로 자동으로 제거된다.

        postRepository.deleteById(id);
        log.info("DELETE Post SUCCESS. Post. id: {}", id);
        return true;
    }
}