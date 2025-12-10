package com.example.modam.domain.friend.Presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendRequestDto {

    @NotNull(message = "대상 사용자 ID는 필수 값입니다.")
    private Long targetUserId;
}