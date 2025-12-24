package com.example.modam.domain.user.Presentation.dto;

import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String name;
    private String nickname;
    private Integer goalScore;
    private String preferredCategories;
    private String profileImageUrl;
    private boolean isPublic;
    private boolean isOnboardingCompleted;
    private UserStatus status;

    public static UserProfileResponse from(UserEntity user){
        return UserProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .goalScore(user.getGoalScore())
                .preferredCategories(user.getPreferredCategories())
                .profileImageUrl(user.getProfileImageUrl())
                .isPublic(user.isPublic())
                .isOnboardingCompleted(user.isOnboardingCompleted())
                .status(user.getStatus())
                .build();
    }
}
