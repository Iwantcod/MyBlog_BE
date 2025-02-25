package com.example.MyBlog.domain.follow.service;

import com.example.MyBlog.domain.follow.DTO.RequestFollowDTO;
import com.example.MyBlog.domain.follow.DTO.ResponseFollowDTO;
import com.example.MyBlog.domain.follow.entity.Follow;
import com.example.MyBlog.domain.follow.repository.FollowRepository;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public FollowService(FollowRepository followRepository, MemberRepository memberRepository) {
        this.followRepository = followRepository;
        this.memberRepository = memberRepository;
    }

    private ResponseFollowDTO toDto(Follow follow) {
        ResponseFollowDTO responseFollowDTO = new ResponseFollowDTO();
        responseFollowDTO.setMemberId(follow.getMember().getId());
        responseFollowDTO.setTargetId(follow.getTarget().getId());
        return responseFollowDTO;
    }

    @Transactional
    public boolean follow(RequestFollowDTO requestFollowDTO) {
        if(followRepository.findByMemberIdAndTargetId(requestFollowDTO.getMemberId(), requestFollowDTO.getTargetId()).isPresent()) {
            // 이미 팔로우 정보가 존재하면 기각
            log.error("Already Follow. MemberId: {}, TargetId: {}", requestFollowDTO.getMemberId(), requestFollowDTO.getTargetId());
            return false;
        }
        Optional<Member> member = memberRepository.findById(requestFollowDTO.getMemberId());
        Optional<Member> target = memberRepository.findById(requestFollowDTO.getTargetId());
        if(member.isEmpty() || target.isEmpty()) {
            log.error("member or target is empty. MemberId: {}, TargetId: {}", requestFollowDTO.getMemberId(), requestFollowDTO.getTargetId());
            return false;
        }

        Follow follow = new Follow();
        follow.setMember(member.get());
        follow.setTarget(target.get());
        followRepository.save(follow);
        // 팔로우하는 사람의 팔로잉 카운트 1 증가
        member.get().setFollowingCnt(member.get().getFollowingCnt() + 1);
        // 팔로잉 대상의 팔로워 카운트 1 증가
        target.get().setFollowersCnt(target.get().getFollowersCnt() + 1);
        memberRepository.save(member.get());
        memberRepository.save(target.get());
        return true;
    }

    @Transactional
    public boolean unfollow(RequestFollowDTO requestFollowDTO) {
        Optional<Member> member = memberRepository.findById(requestFollowDTO.getMemberId());
        Optional<Member> target = memberRepository.findById(requestFollowDTO.getTargetId());
        if(member.isEmpty() || target.isEmpty()) {
            log.error("member or target is empty. MemberId: {}, TargetId: {}", requestFollowDTO.getMemberId(), requestFollowDTO.getTargetId());
            return false;
        }

        // 팔로우하는 사람의 팔로잉 카운트 1 감소
        member.get().setFollowingCnt(member.get().getFollowingCnt() - 1);
        // 팔로잉 대상의 팔로워 카운트 1 감소
        target.get().setFollowersCnt(target.get().getFollowersCnt() - 1);
        memberRepository.save(member.get());
        memberRepository.save(target.get());
        // 팔로우 정보를 삭제하는 '벌크 연산' 후에는 영속성 컨텍스트를 초기화 해줘야 하므로, 벌크 연산 전에 회원 정보 업데이트를 진행한다.
        // 벌크 연산 이후에 업데이트 코드가 위치하면 select 쿼리가 또다시 나간다.


        followRepository.deleteByMemberIdAndTargetId(requestFollowDTO.getMemberId(), requestFollowDTO.getTargetId());
        // 벌크연산으로 인해 발생할 수 있는 영속성 컨텍스트와 DB의 불일치 해소
        entityManager.flush(); // Persistence Context에 pending상태로 저장된 변경사항이 실제 DB에 전송되어 반영
        entityManager.clear(); // 엔티티 매니저가 관리하는 모든 엔티티 인스턴스를 제거 -> DB와 영속성 컨텍스트 사이의 불일치 문제 해결(방지)
        return true;
    }

    // 특정 유저가 팔로우하는 목록을 조회
    @Transactional(readOnly = true)
    public List<ResponseFollowDTO> getTargets(Long memberId) {
        List<Follow> targetList = followRepository.findAllByMemberId(memberId);
        if(targetList == null) {
            log.info("No targets");
            return null;
        }

        List<ResponseFollowDTO> responseFollowDTOList = new ArrayList<>();
        for (Follow follow : targetList) {
            responseFollowDTOList.add(toDto(follow));
        }
        return responseFollowDTOList;
    }

    // 특정 유저를 팔로우하는 목록 조회
    @Transactional(readOnly = true)
    public List<ResponseFollowDTO> getFollowers(Long targetId) {
        List<Follow> followerList = followRepository.findAllByTargetId(targetId);
        if(followerList == null) {
            log.info("No followers");
            return null;
        }
        List<ResponseFollowDTO> responseFollowDTOList = new ArrayList<>();
        for (Follow follow : followerList) {
            responseFollowDTOList.add(toDto(follow));
        }
        return responseFollowDTOList;
    }
}
