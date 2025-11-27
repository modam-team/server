package com.example.modam.domain.user.Presentation;

import com.example.modam.domain.user.Application.UserService;
import com.example.modam.domain.user.Presentation.OnboardingRequest;
import com.example.modam.domain.user.Presentation.NicknameCheckResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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

    @PostMapping("/onboarding/complete")
    public ResponseEntity<Void> completeOnboarding(
            @RequestBody @Valid OnboardingRequest request,
            @RequestHeader (name="X-User-Id") Long userId) {

        userService.completeOnboarding(userId, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @RequestHeader(name="X-User-Id") Long userId) {
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/onboarding/status")
    public ResponseEntity<OnboardingStatusResponse> getOnboardingStatus(
            @RequestHeader(name="X-User-Id") Long userId){
        OnboardingStatusResponse response = userService.getOnboardingStatus(userId);

        return ResponseEntity.ok(response);
    }
}
