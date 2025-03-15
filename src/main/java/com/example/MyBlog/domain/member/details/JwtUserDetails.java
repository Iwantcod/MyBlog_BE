package com.example.MyBlog.domain.member.details;

import com.example.MyBlog.domain.member.DTO.AuthDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// JwtUserDetailsService가 유저 정보를 찾은 다음 반환할때 사용하는 틀(DTO 느낌임)
public class JwtUserDetails implements UserDetails {
    private final AuthDTO authDTO;
    public JwtUserDetails(AuthDTO authDTO) {
        this.authDTO = authDTO;
    }



    @Override // 사용자 권한을 GrantedAuthority 타입 컬렉션으로 반환하는 이유: 사용자가 여러 권한을 가질 수도 있기 때문.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return authDTO.getRoleType();
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return authDTO.getPassword();
    }

    @Override
    public String getUsername() {
        return authDTO.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
