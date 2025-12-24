package com.example.modam.domain.user.Presentation;

import com.example.modam.domain.user.Application.UserService;
import com.example.modam.domain.user.Presentation.dto.*;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.response.ResponseDTO;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            description = "로그인한 사용자의 현재 프로필 정보를 조회합니다."
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

    @Operation(
            summary = "프로필 사진 업로드 및 변경",
            description = "프로필 이미지를 S3에 업로드하고 DB에 URL을 저장합니다"
    )
    @PostMapping("/profile/image")
    public ResponseEntity<Void> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart("imageFile")MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new ApiException(ErrorDefine.FILE_IS_EMPTY);
        }

        Long userId = user.getUser().getId();
        userService.updateProfileImage(userId, file);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "프로필 사진 삭제",
            description = "현재 프로필 이미지를 S3에서 삭제하고, DB URL을 null로 초기화합니다."
    )
    @DeleteMapping("/profile/image")
    public ResponseEntity<Void> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUser().getId();
        userService.deleteProfileImage(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary="프로필 일반 수정",
            description = "로그인한 사용자의 닉네임, 공개 여부 등 프로필 정보를 수정합니다."
    )
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestBody @Valid UpdateProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails user){
        Long userId = user.getUser().getId();
        userService.updateProfile(userId, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary="회원탈퇴",
            description ="로그인한 사용자의 계정을 영구적으로 삭제하고 연관 데이터를 정리합니다."
    )
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUser().getId();
        userService.withdraw(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "계정 활성화",
            description = "사용자 상태를 ACTIVE로 변경하고 탈퇴 신청을 철회합니다."
    )
    @PatchMapping("/activate")
    public ResponseEntity<ResponseDTO<String>> activateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.activateUserStatus(userDetails.getUserId());
        return ResponseEntity.ok(new ResponseDTO<>("계정이 성공적으로 활성화되었습니다."));
    }
}
