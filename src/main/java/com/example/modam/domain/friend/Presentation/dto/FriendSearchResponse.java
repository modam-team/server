package com.example.modam.domain.friend.Presentation.dto;

import com.example.modam.domain.user.Domain.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendSearchResponse {

    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private FriendRelationStatus relationStatus;

    public static FriendSearchResponse from(UserEntity user, FriendRelationStatus status) {
        return FriendSearchResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .relationStatus(status)
                .build();
    }
}
