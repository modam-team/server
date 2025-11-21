package com.example.modam.global.security.jwt;

import com.example.modam.global.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder; // ★ 추가된 Import
import org.springframework.security.core.userdetails.UserDetails; // ★ 추가된 Import
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException{
        String token = resolveToken(request);

        // JWT 유효성 검증
        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)){

            // 토큰에 저장된 Subject(이메일)를 꺼내옵니다.
            String email = jwtProvider.getUserId(token);

            // 유저 정보 생성
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            if (userDetails != null){
                // UserDetails, Password, Role 정보를 기반으로 접근 권한을 가지고 있는 Token 생성
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Security Context 해당 접근 권한 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 다음 필터로 넘기기
        filterChain.doFilter(request, response);

    }

    // Request Header에서 토큰 조회 및 Bearer 문자열 제거 후 반환하는 메소드
    private String resolveToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");

        // token 정보 존재 여부 및 bearer 토큰인지 확인
        if (token!=null && token.startsWith("Bearer ")){
            return token.substring(7);
        }

        return null;
    }
}