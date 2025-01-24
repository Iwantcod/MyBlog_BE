package com.example.MyBlog.domain.member.service;

import com.example.MyBlog.domain.member.DTO.AuthDTO;
import com.example.MyBlog.domain.member.details.JwtUserDetails;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Autowired
    public JwtUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override // 유저네임을 통해 유저 검색 후 JwtUserDetails 인스턴스에 담은 뒤 그것을 반환
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByUsername(username);

        if(member.isPresent()) {
            AuthDTO authDTO = new AuthDTO();
            authDTO.setUsername(username);
            authDTO.setPassword(member.get().getPassword());
            authDTO.setRoleType(member.get().getRoleType().toString());
            return new JwtUserDetails(authDTO);
        } else {
            return null;
        }
    }
}
