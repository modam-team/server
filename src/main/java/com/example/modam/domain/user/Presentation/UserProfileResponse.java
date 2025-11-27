package com.example.modam.domain.user.Presentation;

import com.example.modam.domain.user.Domain.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private final Long userId;
    private final String name;
    private final String nickname;
    private final Integer goalScore;
    private final boolean isOnboardingCompleted;

    public static UserProfileResponse from(UserEntity user){
        return UserProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .goalScore(user.getGoalScore())
                .isOnboardingCompleted(user.isOnboardingCompleted())
                .build();
    }
}
