package com.example.MyBlog.domain.oauth.details;

public interface OAuth2UserInfo {
    String getProvider();
    String getProviderId();
    String getName();
    String getEmail();
}
