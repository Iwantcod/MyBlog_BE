package com.example.MyBlog.domain.oauth.details;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GoogleUserDetails implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "google";
    }
    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
