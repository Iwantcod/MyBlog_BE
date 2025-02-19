package com.example.MyBlog.domain.comment.service;

import com.example.MyBlog.domain.comment.DTO.RequestCommentDTO;
import com.example.MyBlog.domain.comment.DTO.ResponseCommentDTO;
import com.example.MyBlog.domain.comment.entity.Comment;
import com.example.MyBlog.domain.comment.repository.CommentRepository;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
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

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository, MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private ResponseCommentDTO toDTO(Comment comment) {
        ResponseCommentDTO responseCommentDTO = new ResponseCommentDTO();
        responseCommentDTO.setId(comment.getId());
        responseCommentDTO.setMemberUsername(comment.getMemberUsername());
        responseCommentDTO.setPostId(comment.getPost().getId()); // post 조회
        responseCommentDTO.setParentCommentId(comment.getParentComment().getId()); // comment 조회
        responseCommentDTO.setDepth(comment.getDepth());
        responseCommentDTO.setContent(comment.getContent());
        responseCommentDTO.setCreatedAt(comment.getCreatedAt());
        return responseCommentDTO;
    }


    @Transactional // 댓글 작성 성공 여부를 떠나서 게시글 새로고침
    public boolean addComment(RequestCommentDTO requestCommentDTO) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Post> post = postRepository.findById(requestCommentDTO.getPostId());
        if(post.isEmpty()) {
            log.error("Create Comment FAIL: Cannot found post. post id: {}", requestCommentDTO.getPostId());
            return false;
        }
        Optional<Member> member = memberRepository.findById(requestCommentDTO.getMemberId());
        if(member.isEmpty() || !member.get().getUsername().equals(authUsername)) {
            log.error("Create Comment FAIL: Authorization information mismatch. member id: {}", requestCommentDTO.getMemberId());
            return false;
        }

        Comment comment = new Comment();

        if(requestCommentDTO.getParentCommentId() != null) {
            // 부모 댓글이 존재하는 경우
            Optional<Comment> parentComment = commentRepository.findById(requestCommentDTO.getParentCommentId());
            if(parentComment.isEmpty()) {
                log.error("Create Comment Error: Cannot found parent comment. post id: {}", requestCommentDTO.getPostId());
                return false;
            }
            comment.setParentComment(parentComment.get());
            comment.setDepth(parentComment.get().getDepth() + 1);
        }

        comment.setPost(post.get());
        comment.setMember(member.get());
        comment.setContent(requestCommentDTO.getContent());
        // 댓글 저장
        Comment savedComment = commentRepository.save(comment);
        // 저장 후 식별자 발급받으면 그 식별자를 이용하여 자신의 ThreadPath 생성
        if(requestCommentDTO.getParentCommentId() == null) {
            savedComment.setThreadPath(savedComment.getId().toString());
        } else {
            // 부모댓글이 존재하는 경우, 식별자와 부모의 ThreadPath를 이용하여 자신의 ThreadPath 생성
            savedComment.setThreadPath(savedComment.getParentComment().getThreadPath() + "." + savedComment.getId().toString());
        }
        // 댓글 업데이트
        commentRepository.save(savedComment);
        // 댓글 수 카운트 1 증가: 게시글 업데이트
        post.get().setCommentsCount(post.get().getCommentsCount() + 1);
        postRepository.save(post.get());
        log.info("Create Comment SUCCESS: Post's Comments Count Increase. post id: {}", requestCommentDTO.getPostId());
        return true;
    }

    @Transactional(readOnly = true)
    public List<ResponseCommentDTO> getCommentsByPostIdPaging(Long postId, Integer startOffset) {
        // 프론트로부터 1,2,3,4... 의 값을 받는다. 이 값에 15을 곱한 값이 조회 시작지점이다.
        int pageSize = 15;

        // 프론트에서는 (1 ~ )의 값을 받지만, 데이터베이스에 쿼리문을 날릴땐 당연히 0부터 시작한다.
        int startPage = startOffset - 1;
        Pageable pageable = PageRequest.of(startPage, pageSize, Sort.by("threadPath").ascending());
        Page<Comment> comments = commentRepository.findByPostIdPaging(postId, pageable);
        if(comments.isEmpty()) {
            log.error("GET Comment FAIL: Cannot found comment. post id: {}", postId);
            return null;
        }

        Page<ResponseCommentDTO> dtoPage = comments.map(comment -> {
            ResponseCommentDTO responseCommentDTO = new ResponseCommentDTO();
            responseCommentDTO.setId(comment.getId());
            responseCommentDTO.setPostId(comment.getPost().getId());
            responseCommentDTO.setDepth(comment.getDepth());

            if(comment.getParentComment() != null) {
                responseCommentDTO.setParentCommentId(comment.getParentComment().getId());
            }


            if(!comment.isDeleted()) {
                // 삭제된 댓글이 아닌 경우에만 추가적인 세부 정보를 반환
                responseCommentDTO.setMemberUsername(comment.getMemberUsername());
                responseCommentDTO.setContent(comment.getContent());
                responseCommentDTO.setCreatedAt(comment.getCreatedAt());
            } else {
                responseCommentDTO.setContent("삭제된 댓글입니다.");
            }
            return responseCommentDTO;
        });
        log.info("GET Comment SUCCESS.");
        return dtoPage.getContent();
    }

    @Transactional // 댓글 수정 시 바뀌는건 댓글의 내용 뿐이므로, 그 내용만 인자로 넘겨받는다.
    public boolean updateComment(String updatedContent, Long commentId) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Comment> comment = commentRepository.findById(commentId);
        if(comment.isEmpty()) {
            log.error("UPDATE Comment FAIL: Cannot found comment with id: {}", commentId);
            return false;
        }
        if(!comment.get().getMemberUsername().equals(authUsername)) {
            // 댓글 수정 권한 검사(댓글 작성자인지 검증)
            log.error("UPDATE Comment FAIL: Authorization information mismatch. comment id: {}", commentId);
            return false;
        }

        comment.get().setContent(updatedContent);
        commentRepository.save(comment.get());
        return true;
    }

    @Transactional // 댓글 삭제 메소드 -> 실제 삭제가 아닌, '삭제여부' 컬럼을 true로 변경
    public boolean deleteComment(Long commentId) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Comment> comment = commentRepository.findById(commentId);
        if(comment.isEmpty()) {
            log.error("Delete Comment FAIL: Cannot found comment with id: {}", commentId);
            return false;
        }
        if(!comment.get().getMemberUsername().equals(authUsername)) {
            // 댓글 수정 권한 검사(댓글 작성자인지 검증)
            log.error("DELETE Comment FAIL: Authorization information mismatch. comment id: {}", commentId);
            return false;
        }

        Optional<Post> post = postRepository.findById(comment.get().getPost().getId());
        if(post.isEmpty()) {
            log.error("DELETE Comment FAIL: Cannot found post with id: {}", post.get().getId());
            return false;
        }

        // 댓글은 실제로 삭제하는 것이 아니라, '삭제여부' 컬럼을 true로 변경만 한다.
        // 백업 목적은 아니고, 삭제된 댓글 정보가 있어야 삭제된 댓글의 대댓글 정보를 올바르게 조회할 수 있기 때문이다.
        // 삭제된 댓글의 대댓글들의 외래키 참조 무결성 제약조건도 편하게 지킬 수 있다.
        comment.get().setDeleted(true);
        commentRepository.save(comment.get());
        post.get().setCommentsCount(post.get().getCommentsCount() - 1);
        postRepository.save(post.get());
        return true;
    }
}
