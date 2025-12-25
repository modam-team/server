package com.example.modam.jwt;

import com.example.modam.global.security.jwt.JwtProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.lang.reflect.Method;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenTest {

    private final JwtProvider jwtProvider = new JwtProvider();

    @DisplayName("JWT에서 userId 파싱 테스트")
    @Test
    void parse_user_id() throws Exception {

        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Secret = Encoders.BASE64.encode(key.getEncoded());

        ReflectionTestUtils.setField(jwtProvider, "secretkey", base64Secret);
        ReflectionTestUtils.setField(jwtProvider, "accessTokenExpirationTime", 100000L);
        ReflectionTestUtils.setField(jwtProvider, "refreshTokenExpirationTime", 200000L);

        Method initMethod = JwtProvider.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(jwtProvider);

        Long id = 4553622369L;
        String token = Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 100000L))
                .signWith(key)
                .compact();

        Long result = jwtProvider.getUserId(token);
        assertThat(result).isEqualTo(id);
    }
}
