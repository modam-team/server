package com.example.modam.domain.user.Application;


import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.domain.user.Presentation.OnboardingRequest;
import com.example.modam.domain.user.Presentation.OnboardingStatusResponse;
import com.example.modam.domain.user.Presentation.UserProfileResponse;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean checkNicknameDuplication(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    // 온보딩 최종 제출
    @Transactional
    public void completeOnboarding(Long userId, OnboardingRequest request){
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
}
