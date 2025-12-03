package com.example.modam.domain.user.Presentation;

import com.example.modam.domain.user.Application.UserService;
import com.example.modam.domain.user.Presentation.dto.NicknameCheckResponse;
import com.example.modam.domain.user.Presentation.dto.OnboardingRequest;
import com.example.modam.domain.user.Presentation.dto.OnboardingStatusResponse;
import com.example.modam.domain.user.Presentation.dto.UserProfileResponse;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "사용자 관련 API")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "닉네임 중복 확인",
            description = "사용자가 입력한 닉네임의 중복 여부를 확인합니다."
    )
    @GetMapping("/nickname/check")
    public ResponseEntity<NicknameCheckResponse> checkNickname(
            @RequestParam String nickname){

        boolean isDuplicated = userService.checkNicknameDuplication(nickname);

        NicknameCheckResponse response = new NicknameCheckResponse(
                !isDuplicated, // 중복 아니면 true
                isDuplicated ? "이미 사용 중인 닉네임입니다." : "사용 가능한 닉네임입니다."
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "온보딩 완료 및 정보 저장",
            description = "로그인한 사용자의 닉네임, 목표 권수 등 온보딩 정보를 최종 저장하고 온보딩을 완료 처리합니다."
    )
    @PostMapping("/onboarding/complete")
    public ResponseEntity<Void> completeOnboarding(
            @RequestBody @Valid OnboardingRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUser().getId();
        userService.completeOnboarding(userId, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "사용자 프로필 조회",
            description = "로그인한 사용자의 현재 프로필 정보(닉네임, 목표 권수 등)를 조회합니다."
    )
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @AuthenticationPrincipal CustomUserDetails user){
        Long userId = user.getUser().getId();
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "온보딩 완료 상태 조회",
            description = "로그인한 사용자가 온보딩을 완료했는지 여부(true/false)를 조회합니다."
    )
    @GetMapping("/onboarding/status")
    public ResponseEntity<OnboardingStatusResponse> getOnboardingStatus(
            @AuthenticationPrincipal CustomUserDetails user){
        Long userId = user.getUser().getId();

        OnboardingStatusResponse response = userService.getOnboardingStatus(userId);

        return ResponseEntity.ok(response);
    }
}
