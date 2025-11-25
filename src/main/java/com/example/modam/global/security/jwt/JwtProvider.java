package com.example.modam.global.security.jwt;

import com.example.modam.domain.auth.dto.TokenResponse;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretkey;
    private SecretKey key;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    @PostConstruct
    protected void init() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretkey);
        this.key = Keys.hmacShaKeyFor(secretKeyBytes);
    }
    public TokenResponse createToken(String principalId, String role){
        String accessToken = createTokenInternal(principalId, role, expirationTime);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(expirationTime)
                .build();
    }
    // jwt 생성
    public String createTokenInternal(String principalId, String role, long expirationTime) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(principalId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .setAllowedClockSkewSeconds(300)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ApiException(ErrorDefine.TOKEN_EXPIRED);
        } catch (SecurityException | SignatureException e) {
            throw new ApiException(ErrorDefine.TOKEN_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new ApiException(ErrorDefine.TOKEN_MALFORMED);
        } catch (Exception e) {
            throw new ApiException(ErrorDefine.TOKEN_INVALID);
        }
    }

    // 토큰에서 아이디(Subject) 추출
    public String getUserId(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}