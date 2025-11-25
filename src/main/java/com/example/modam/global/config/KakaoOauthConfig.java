package com.example.modam.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kakao.oauth2")
public class KakaoOauthConfig {

    private String clientId;
    private String redirectUri;
    private String tokenUri;
    private String userInfoUri;
    private String scope;
    private String clientSecret;
}
