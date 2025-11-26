package com.example.modam.domain.auth;

import com.example.modam.domain.auth.dto.TokenResponse;
import com.example.modam.global.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    //카카오 로그인 API
    // [POST] /api/v1/auth/kakao/login?code={인가코드}
    @PostMapping("/kakao/login")
    public ResponseEntity<ResponseDTO<TokenResponse>> kakaoLogin(@RequestParam("code") String code) {
        TokenResponse tokenResponse = authService.kakaoLogin(code);
        return ResponseEntity.ok(new ResponseDTO<>(tokenResponse));
    }
}