package com.example.modam.domain.user.Presentation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class OnboardingRequest {
    @NotBlank(message="닉네임을 입력해주세요.")
    private String nickname;

    @NotNull(message="목표 권수를 입력해주세요.")
    @Min(value=1, message = "목표 권수는 1권 이상이어야 합니다.")
    private Integer goalScore;

    @NotNull(message = "선호 분야를 하나 이상 선택해야 합니다.")
    private List<Long> categoryIds;
}
