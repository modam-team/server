package com.example.modam.domain.user.Application;


import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.domain.user.Presentation.OnboardingRequest;
import com.example.modam.domain.user.Presentation.OnboardingStatusResponse;
import com.example.modam.domain.user.Presentation.UserProfileResponse;
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
        // 1. 사용자 엔티티 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 닉네임 중복 확인
        if (user.getNickname() != null &&
                !user.getNickname().equals(request.getNickname()) &&
                userRepository.existsByNickname(request.getNickname())){
            throw new IllegalStateException("이미 사용 중인 닉네임 입니다. 다시 확인해주세요.");
        }

        // 3. UserEntity 업데이트
        user.updateOnboardingInfo(
                request.getNickname(),
                request.getGoalScore(),
                request.getCategories());
    }

    // 사용자 프로필 조회 메서드
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserProfileResponse.from(user);
    }

    // 사용자 온보딩 상태 조회 메서드
    @Transactional(readOnly = true)
    public OnboardingStatusResponse getOnboardingStatus(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return OnboardingStatusResponse.from(user.isOnboardingCompleted());
    }
}
