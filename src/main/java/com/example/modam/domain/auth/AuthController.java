package com.example.modam.domain.auth;

import com.example.modam.domain.auth.dto.TokenResponse;
import com.example.modam.global.response.ResponseDTO;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

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

    @Operation(
            summary = "토큰 재발급",
            description = "리프레시 토큰을 사용하여 액세스 토큰을 재발급합니다."
    )
    @PostMapping("/reissue")
    public ResponseEntity<ResponseDTO<TokenResponse>> reissue(@RequestParam("refreshToken") String refreshToken){
        TokenResponse tokenResponse = authService.reissue(refreshToken);
        return ResponseEntity.ok(new ResponseDTO<>(tokenResponse));
    }

    @Operation(
            summary = "로그아웃",
            description = "redis에서 리프레시 토큰을 삭제하여 로그아웃 처리합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<String>> logout(@AuthenticationPrincipal CustomUserDetails userDetails){
        authService.logout(userDetails.getUserId());
        return ResponseEntity.ok(new ResponseDTO<>("로그아웃 성공"));
    }

}