package com.example.MyBlog.domain.likes.service;

import com.example.MyBlog.domain.likes.DTO.RequestLikesDTO;
import com.example.MyBlog.domain.likes.DTO.ResponseLikesDTO;
import com.example.MyBlog.domain.likes.entity.Like;
import com.example.MyBlog.domain.likes.repository.LikeRepository;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import com.example.MyBlog.domain.post.entity.Post;
import com.example.MyBlog.domain.post.repository.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LikesService {
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public LikesService(LikeRepository likeRepository, MemberRepository memberRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    private ResponseLikesDTO toDTO(Like like) {
        ResponseLikesDTO responseLikesDTO = new ResponseLikesDTO();
        responseLikesDTO.setId(like.getId());
        responseLikesDTO.setPostId(like.getPost().getId());
        responseLikesDTO.setMemberId(like.getMember().getId());
        responseLikesDTO.setMemberUsername(like.getMember().getUsername());
        responseLikesDTO.setCreatedAt(like.getCreatedAt());
        return responseLikesDTO;
    }

    @Transactional
    public void addLike(RequestLikesDTO requestLikesDTO) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> member = memberRepository.findById(requestLikesDTO.getMemberId());
        if(member.isEmpty()) {
            log.error("ADD Like FAIL: Member not found. Member Id: {}", requestLikesDTO.getMemberId());
            return;
        }
        if(!member.get().getUsername().equals(authUsername)) {
            log.error("ADD Like FAIL: Authorization information mismatch. Member id: {}", requestLikesDTO.getMemberId());
            return;
        }

        Optional<Post> post = postRepository.findById(requestLikesDTO.getPostId());
        if(post.isEmpty()) {
            log.error("ADD Like FAIL: Post not found. Post Id: {}", requestLikesDTO.getPostId());
            return;
        }

        Optional<Like> isExist = likeRepository.findByMemberIdAndPostId(requestLikesDTO.getMemberId(), requestLikesDTO.getPostId());
        if(isExist.isPresent()) { // 좋아요를 이미 누른 상태라면, isExist에 값이 있다는 의미이다.
            return;
        }
        Like like = new Like();
        like.setPost(post.get());
        like.setMember(member.get());
        likeRepository.save(like);
        // 게시글의 좋아요 카운트 1 증가
        post.get().setLikesCount(post.get().getLikesCount() + 1);
        postRepository.save(post.get());
    }

    @Transactional
    public void removeLike(RequestLikesDTO requestLikesDTO) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> member = memberRepository.findById(requestLikesDTO.getMemberId());
        if(member.isEmpty()) {
            log.error("DELETE Like FAIL: Member not found. Member Id: {}", requestLikesDTO.getMemberId());
            return;
        }
        if(!member.get().getUsername().equals(authUsername)) {
            log.error("DELETE Like FAIL: Authorization information mismatch. Member id: {}", requestLikesDTO.getMemberId());
            return;
        }
        Optional<Post> post = postRepository.findById(requestLikesDTO.getPostId());
        if(post.isEmpty()) {
            log.error("DELETE Like FAIL: Post not found. Post Id: {}", requestLikesDTO.getPostId());
            return;
        }

        // memberId, postId를 통해 좋아요 정보 제거
        likeRepository.deleteByMemberIdAndPostId(requestLikesDTO.getMemberId(), requestLikesDTO.getPostId());
        // 게시글의 좋아요 카운트 1 감소
        post.get().setLikesCount(post.get().getLikesCount() - 1);
        postRepository.save(post.get());
        // 벌크연산으로 인해 발생할 수 있는 영속성 컨텍스트와 DB의 불일치 해소
        entityManager.flush(); // Persistence Context에 pending상태로 저장된 변경사항이 실제 DB에 전송되어 반영
        entityManager.clear(); // 엔티티 매니저가 관리하는 모든 엔티티 인스턴스를 제거 -> DB와 영속성 컨텍스트 사이의 불일치 문제 해결(방지)
    }


    // 특정 게시글의 좋아요 정보 조회
    @Transactional(readOnly = true)
    public List<ResponseLikesDTO> getLikesByPostId(Long postId, Integer startOffset) {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()) {
            log.error("GET Like FAIL: Post not found. Post Id: {}", postId);
            return null;
        }

        // 프론트로부터 1,2,3,4... 의 값을 받는다. 이 값에 10을 곱한 값이 조회 시작지점이다.
        int pageSize = 15;

        // 프론트에서는 (1 ~ )의 값을 받지만, 데이터베이스에 쿼리문을 날릴땐 당연히 0부터 시작한다.
        int startPage = startOffset - 1;
        Pageable pageable = PageRequest.of(startPage, pageSize, Sort.by("createdAt").ascending());
        Page<Like> likes = likeRepository.findAllByPostId(postId, pageable);
        Page<ResponseLikesDTO> dtoPage = likes.map(this::toDTO);

        return dtoPage.getContent();
    }



    @Transactional(readOnly = true)
    public List<Long> getDefaultLikesInfo(Long memberId) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.isEmpty()) {
            log.error("GET Like FAIL: Member not found. Member Id: {}", memberId);
            return null;
        }
        if(!member.get().getUsername().equals(authUsername)) {
            log.error("GET Like FAIL: Authorization information mismatch. Member id: {}", memberId);
            return null;
        }

        List<Like> likes = likeRepository.findAllByMemberId(memberId);
        if(likes == null) {
            return null;
        }
        List<Long> postIds = new ArrayList<>();
        for (Like like : likes) {
            log.info("No Post from This Member: Member Id: {}", memberId);
            postIds.add(like.getPost().getId());
        }
        return postIds;
    }

}
