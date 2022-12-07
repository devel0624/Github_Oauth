package com.nhnacademy.security.service;

//TODO 03 Custom UserDetailsService 구현하기 (JPA 기술 사용)

import com.nhnacademy.security.entity.Member;
import com.nhnacademy.security.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        return new User(member.getId(),member.getPwd(),
                Collections.singletonList(new SimpleGrantedAuthority(member.getAuthority().getAuthority())));

    }
}
