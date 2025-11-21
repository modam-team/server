package com.example.modam.global.config;

import com.example.modam.domain.auth.CustomOAuth2UserService;
import com.example.modam.global.security.handler.OAuth2AuthenticationSuccessHandler; // 1단계에서 만든 클래스 import
import com.example.modam.global.security.jwt.JwtAccessDeniedHandler;
import com.example.modam.global.security.jwt.JwtAuthenticationEntryPoint;
import com.example.modam.global.security.jwt.JwtFilter;
import com.example.modam.global.security.jwt.ExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ExceptionFilter exceptionFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용 방식 수정 권장
                .csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // httpBasic, httpFormLogin 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        // JWT 관련 필터 설정 및 예외 처리
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionFilter, JwtFilter.class);

        // 요청 URI별 권한 설정
        http.authorizeHttpRequests((authorize) ->
                // Swagger UI 외부 접속 허용
                authorize.requestMatchers( "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // 로그인 로직 접속 허용
                        .requestMatchers("/v1/auth/**").permitAll()
                        // 임시로 책장 api는 인증 없이 허용
                        .requestMatchers("/v1/books/**").permitAll()
                        // DefaultExceptionHandler 처리를 위한 error PermitAll
                        .requestMatchers("/error/**").permitAll()
                        // 이외의 모든 요청은 인증 정보 필요
                        .anyRequest().authenticated());

        // OAuth2 로그인 설정 활성화
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2AuthenticationSuccessHandler)
        );

        return http.build();
    }

    // CORS 허용하도록 customizing 진행
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        // 인증정보 주고받도록 허용
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("https://hwangrock.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // 비밀번호 암호화 방식 설정
        return new BCryptPasswordEncoder();
    }
}
