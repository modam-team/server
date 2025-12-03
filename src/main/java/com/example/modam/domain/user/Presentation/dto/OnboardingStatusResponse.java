package com.example.modam.domain.user.Presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OnboardingStatusResponse {

    private final boolean isOnboardingCompleted;

    public static OnboardingStatusResponse from(boolean status){
        return OnboardingStatusResponse.builder()
                .isOnboardingCompleted(status)
                .build();
    }
}
