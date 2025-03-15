package com.example.MyBlog.domain.oauth.details;

import com.example.MyBlog.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOauth2UserDetails implements OAuth2User {
    private final Member member;
    private Map<String, Object> attributes;

    public CustomOauth2UserDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRoleType().name();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return member.getName();
    }

    public Member getMember() {
        return member;
    }
}
