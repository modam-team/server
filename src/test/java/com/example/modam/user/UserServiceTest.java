package com.example.modam.user;

import com.example.modam.domain.user.Application.UserService;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Domain.UserStatus; // UserStatus import
import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.domain.user.Presentation.dto.OnboardingRequest;
import com.example.modam.domain.user.Presentation.dto.UpdateProfileRequest;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.utils.S3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private UserService userService;

    private final Long TEST_USER_ID = 1L;
    private final Long NON_EXISTENT_ID = 999L;

    private UserEntity mockUser(Long id, String nickname, boolean onboardingCompleted) {
        return UserEntity.builder()
                .id(id)
                .name("모다미")
                .providerId(String.valueOf(id))
                .nickname(nickname)
                .isOnboardingCompleted(onboardingCompleted)
                .isPublic(true)
                .status(UserStatus.ACTIVE) // ACTIVE 상태로 설정
                .build();
    }

    @DisplayName("ID로 사용자 조회 성공 테스트")
    @Test
    void findUserById_Success() {
        UserEntity mockUser = mockUser(TEST_USER_ID, "모다미", true);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));

        UserEntity foundUser = userService.findUserById(TEST_USER_ID);

        assertNotNull(foundUser);
        verify(userRepository, times(1)).findById(TEST_USER_ID);
    }

    @DisplayName("ID로 사용자 조회 실패 (NOT_FOUND) 테스트")
    @Test
    void findUserById_NotFound() {
        when(userRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.findUserById(NON_EXISTENT_ID));

        assertEquals(ErrorDefine.USER_NOT_FOUND, exception.getError());
    }


    @DisplayName("온보딩 완료 성공 테스트")
    @Test
    void completeOnboarding_Success() {
        UserEntity user = mockUser(TEST_USER_ID, "이전모다미", false);
        OnboardingRequest request = new OnboardingRequest("새로운모다미", 100, List.of("소설"));

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("새로운모다미")).thenReturn(false);

        userService.completeOnboarding(TEST_USER_ID, request);

        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(userRepository, times(1)).existsByNickname("새로운모다미");
    }

    @DisplayName("온보딩 재시도 실패 (ALREADY_ONBOARDED) 테스트")
    @Test
    void completeOnboarding_AlreadyCompleted() {
        UserEntity user = mockUser(TEST_USER_ID, "이전모다미", true);
        OnboardingRequest request = new OnboardingRequest("새로운모다미", 100, List.of("소설"));

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.completeOnboarding(TEST_USER_ID, request));

        assertEquals(ErrorDefine.USER_ALREADY_ONBOARDED, exception.getError());
    }


    @DisplayName("프로필 이미지 변경 성공 테스트")
    @Test
    void updateProfileImage_Success_NoOldImage() throws IOException {
        UserEntity user = mockUser(TEST_USER_ID, "모다미", true);
        MockMultipartFile mockFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "data".getBytes());
        String newUrl = "http://s3.url/new-image.jpg";

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(s3Uploader.uploadFile(mockFile, "profile")).thenReturn(newUrl);

        userService.updateProfileImage(TEST_USER_ID, mockFile);

        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(s3Uploader, never()).deleteFile(anyString()); // 기존 이미지 삭제 호출 안됨
        verify(s3Uploader, times(1)).uploadFile(mockFile, "profile");
    }


    @DisplayName("회원 탈퇴 성공 테스트")
    @Test
    void withdraw_Success_SoftDelete() {
        UserEntity user = spy(mockUser(TEST_USER_ID, "모다미", true));
        user.updateProfileImageUrl("http://old.url/image.jpg");

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

        userService.withdraw(TEST_USER_ID);

        verify(s3Uploader, times(1)).deleteFile("http://old.url/image.jpg");
        verify(user, times(1)).requestWithdrawal();
        verify(userRepository, times(1)).save(user);
        assertEquals(UserStatus.WITHDRAWAL_PENDING, user.getStatus());
        assertNotNull(user.getWithdrawalRequestedAt());
    }

    @DisplayName("회원 탈퇴 실패 (NOT_FOUND) 테스트")
    @Test
    void withdraw_UserNotFound() {
        when(userRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());
        ApiException exception = assertThrows(ApiException.class,
                () -> userService.withdraw(NON_EXISTENT_ID));
        assertEquals(ErrorDefine.USER_NOT_FOUND, exception.getError());
        verifyNoInteractions(s3Uploader);
    }

    @DisplayName("온보딩 시 닉네임 중복 실패 테스트")
    @Test
    void completeOnboarding_NicknameDuplication() {
        UserEntity user = mockUser(TEST_USER_ID, "기존닉네임", false);
        OnboardingRequest request = OnboardingRequest.builder()
                .nickname("중복된닉네임")
                .goalScore(100)
                .categories(List.of("역사"))
                .build();

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("중복된닉네임")).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.completeOnboarding(TEST_USER_ID, request));

        assertEquals(ErrorDefine.NICKNAME_DUPLICATION, exception.getError());
        verify(userRepository, times(1)).existsByNickname("중복된닉네임");
    }
}