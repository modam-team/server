package com.example.modam.domain.auth;

import com.example.modam.domain.auth.dto.TokenResponse;
import com.example.modam.global.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "카카오 토큰 발급",
            description = "사용자가 카카오에서 인증코드 발급 후 JWT 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "JWT 발급 성공")
    })
    @PostMapping("/kakao/login")
    public ResponseEntity<ResponseDTO<TokenResponse>> kakaoLogin(@RequestParam("code") String code) {
        TokenResponse tokenResponse = authService.kakaoLogin(code);
        return ResponseEntity.ok(new ResponseDTO<>(tokenResponse));
    }
}