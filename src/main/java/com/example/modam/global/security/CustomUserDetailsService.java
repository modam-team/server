package com.example.modam.global.security;

import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String providerId) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}
