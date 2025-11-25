package com.example.modam.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class KakaoUserInfo {

    // 카카오 회원 번호 (고유 ID)
    private Long id;

    // 카카오 계정 정보
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    // 프로필 정보 (닉네임, 이미지 등)
    @JsonProperty("properties")
    private UserProperties properties;

    // 내부 클래스: 계정 정보
    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {

        @JsonProperty("has_email")
        private Boolean hasEmail;

        @JsonProperty("email")
        private String email;
    }

    // 내부 클래스: 프로필 속성
    @Getter
    @NoArgsConstructor
    public static class UserProperties {

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }
}
