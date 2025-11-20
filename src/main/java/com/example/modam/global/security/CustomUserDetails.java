package com.example.modam.global.security;

import com.example.modam.domain.user.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User{

    private final UserEntity user;
    private Map<String, Object> attributes;

    // 일반 로그인용 생성자
    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    public CustomUserDetails(UserEntity user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // 모든 유저에게 "ROLE_USER" 권한 부여
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

    @Override
    public String getPassword(){
        return "";
    }

    @Override
    public String getUsername(){
        return user.getProviderId();
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // 카카오에서 받은 정보 반환
    }

    @Override
    public String getName() {
        return user.getProviderId(); // 식별자로 providerid 사용
    }
}