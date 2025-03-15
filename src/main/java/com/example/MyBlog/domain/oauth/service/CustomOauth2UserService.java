package com.example.MyBlog.domain.oauth.service;

import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import com.example.MyBlog.domain.oauth.details.CustomOauth2UserDetails;
import com.example.MyBlog.domain.oauth.details.GoogleUserDetails;
import com.example.MyBlog.domain.oauth.details.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    @Autowired
    public CustomOauth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes: {}", oAuth2User.getAttributes());
        // 현재 로그인 요청을 보낸 OAuth2 공급자(예: google)의 식별자(registrationId)를 가져옴
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = null;

        if(provider.equals("google")) {
            log.info("google login");
            oAuth2UserInfo = new GoogleUserDetails(oAuth2User.getAttributes());
        }

        if(oAuth2UserInfo == null) {
            throw new OAuth2AuthenticationException("Unsupported provider");
        }

        String providerId = oAuth2UserInfo.getProviderId();
//            String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        String oAuthId = provider + "_" + providerId;
        Optional<Member> findMember = memberRepository.findByoAuthId(oAuthId);
        Member member;
        if(findMember.isPresent()) {
            member = findMember.get();
        } else {
            member = new Member();
            member.setoAuthId(oAuthId);
            member.setProviderId(providerId);
            member.setProvider(provider);
            member.setName(name);
            member.setPassword("OAuthUser");
            memberRepository.save(member);
        }
        return new CustomOauth2UserDetails(member, oAuth2User.getAttributes());
    }
}
