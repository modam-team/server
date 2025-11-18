package com.example.modam.global.config;

import com.example.modam.domain.auth.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService){
        this.customOAuth2UserService = customOAuth2UserService;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // API 서버에서는 CORS/CSRF 토큰 기반 인증을 주로 사용하므로 비활성화 
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())

            // 테스트를 위해 모든 요청에 대한 권한을 요청함
            // 실제 서비스에서는 여기에 권한 설정을 추가해야 함
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )

            // OAuth 2.0 로그인 기능 활성화
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/oauth/loginInfo", true)
                    .userInfoEndpoint((userInfo->userInfo
                            .userService(customOAuth2UserService)
                    ))
            );

        return http.build();
    }

}
