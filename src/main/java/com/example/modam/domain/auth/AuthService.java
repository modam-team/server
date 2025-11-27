package com.example.modam.domain.auth;

import com.example.modam.domain.auth.dto.KakaoTokenResponse;
import com.example.modam.domain.auth.dto.KakaoUserInfo;
import com.example.modam.domain.auth.dto.TokenResponse;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.global.config.KakaoOauthConfig;
import com.example.modam.global.security.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RestTemplate restTemplate;
    private final KakaoOauthConfig kakaoOauthConfig;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public TokenResponse kakaoLogin(String code) {
        KakaoTokenResponse kakaoToken = getKakaoToken(code);
        KakaoUserInfo kakaoUser = getKakaoUserInfo(kakaoToken.getAccessToken());
        UserEntity user = findOrCreateUser(kakaoUser);
        // 서비스 자체 토큰 발급 (Principal: providerId, Role: "USER")
        return (TokenResponse) jwtProvider.createToken(user.getProviderId(), "USER");
    }

    private KakaoTokenResponse getKakaoToken(String code){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOauthConfig.getClientId());
        params.add("redirect_uri", kakaoOauthConfig.getRedirectUri());
        params.add("code", code);
        if (kakaoOauthConfig.getClientSecret() != null) {
            params.add("client_secret", kakaoOauthConfig.getClientSecret());
        }
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try{
            ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                    kakaoOauthConfig.getTokenUri(),
                    HttpMethod.POST,
                    request,
                    KakaoTokenResponse.class
            );
            return response.getBody();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("카카오 Access Token 발급 실패", e);
        }
    }

    private KakaoUserInfo getKakaoUserInfo(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try{
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    kakaoOauthConfig.getUserInfoUri(),
                    HttpMethod.POST,
                    request,
                    KakaoUserInfo.class
            );
            return response.getBody();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("카카오 사용자 정보 조회 실패", e);
        }
    }

    //서비스 사용자 DB 처리 (회원가입/로그인) 로직
    private UserEntity findOrCreateUser(KakaoUserInfo kakaoUser){
        String providerId = String.valueOf(kakaoUser.getId());

        Optional<UserEntity> optionalUser = userRepository.findByProviderId(providerId);

        if (optionalUser.isPresent()){
            return optionalUser.get();
        } else{
            // 신규 사용자
            UserEntity newUser = UserEntity.builder()
                    .name(kakaoUser.getProperties().getNickname())
                    .providerId(providerId)
                    .build();
            return userRepository.save(newUser);
        }
    }
}