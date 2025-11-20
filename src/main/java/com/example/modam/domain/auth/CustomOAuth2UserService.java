package com.example.modam.domain.auth;

import com.example.modam.domain.user.UserEntity;
import com.example.modam.domain.user.UserRepository;
import com.example.modam.global.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = String.valueOf(attributes.get("id"));

        // 3. 카카오 특화 로직: properties 맵에서 nickname과 ID를 추출합니다.
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String name = (String) profile.get("nickname");

        log.info("카카오 로그인 시도 - id: {}, 이름: {}", providerId, name);

        // DB에 저장
        UserEntity userEntity = userRepository.findByProviderId(providerId)
                .orElseGet(() -> userRepository.save(UserEntity.builder()
                        .providerId(providerId)
                        .name(name)
                        .build()));

        return new CustomUserDetails(userEntity, attributes);
    }
}
