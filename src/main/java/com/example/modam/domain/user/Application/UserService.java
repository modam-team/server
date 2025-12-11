package com.example.modam.domain.user.Application;


import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Interface.UserRepository;

import com.example.modam.domain.user.Presentation.dto.OnboardingStatusResponse;
import com.example.modam.domain.user.Presentation.dto.UpdateProfileRequest;
import com.example.modam.domain.user.Presentation.dto.UserProfileResponse;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.utils.S3Uploader;
import com.example.modam.domain.user.Presentation.dto.OnboardingRequest;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    public boolean checkNicknameDuplication(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    // 온보딩 최종 제출
    @Transactional
    public void completeOnboarding(Long userId, @Valid OnboardingRequest request){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        if (user.isOnboardingCompleted()){
            throw new ApiException(ErrorDefine.USER_ALREADY_ONBOARDED);
        }

        if (user.getNickname() != null &&
                !user.getNickname().equals(request.getNickname()) &&
                userRepository.existsByNickname(request.getNickname())){
            throw new ApiException(ErrorDefine.NICKNAME_DUPLICATION);
        }

        user.updateOnboardingInfo(
                request.getNickname(),
                request.getGoalScore(),
                request.getCategories());
    }

    // 사용자 프로필 조회 메서드
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()->new ApiException(ErrorDefine.USER_NOT_FOUND));

        return UserProfileResponse.from(user);
    }

    // 사용자 온보딩 상태 조회 메서드
    @Transactional(readOnly = true)
    public OnboardingStatusResponse getOnboardingStatus(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        return OnboardingStatusResponse.from(user.isOnboardingCompleted());
    }

    // 프로필 사진 업로드 및 변경
    @Transactional
    public void updateProfileImage(Long userId, MultipartFile file) throws IOException{

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        // 기존 이미지가 있다면 S3에서 삭제하는 로직 추가
        String oldImageUrl = user.getProfileImageUrl();
        if (oldImageUrl != null && !oldImageUrl.isEmpty()){
            s3Uploader.deleteFile(oldImageUrl);
        }

        String imageUrl = s3Uploader.uploadFile(file, "profile");
        user.updateProfileImageUrl(imageUrl);
    }

    // 프로필 사진을 제거하고 기본 이미지로 되돌림
    @Transactional
    public void deleteProfileImage(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new ApiException(ErrorDefine.USER_NOT_FOUND));

        String oldImageUrl = user.getProfileImageUrl();

        if (oldImageUrl != null && !oldImageUrl.isEmpty()){
            s3Uploader.deleteFile(oldImageUrl);
        } else {
            return;
        }
        user.updateProfileImageUrl(null);
    }

    // 프로필 정보 수정
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new ApiException(ErrorDefine.USER_NOT_FOUND));

        if (!user.getNickname().equals(request.getNickname()) &&
                userRepository.existsByNickname(request.getNickname())) {
            throw new ApiException(ErrorDefine.NICKNAME_DUPLICATION);
        }

        user.updateProfileInfo(
                request.getNickname(),
                request.getIsPublic()
        );
    }
    // 회원 탈퇴
    @Transactional
    public void withdraw(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new ApiException(ErrorDefine.USER_NOT_FOUND));

        String oldImageUrl = user.getProfileImageUrl();
        if (oldImageUrl!=null && !oldImageUrl.isEmpty()){
            if (s3Uploader!=null) {
                s3Uploader.deleteFile(oldImageUrl);
            }
        }
        user.requestWithdrawal();
        userRepository.save(user);
    }

    // id로 찾기
    @Transactional(readOnly = true)
    public UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));
    }

    // 닉네임으로 사용자 목록 조회
    public List<UserEntity> findUsersByNicknameFullTextSearch(String keyword) {
        return userRepository.findByNicknameFullTextSearch(keyword);
    }

}
