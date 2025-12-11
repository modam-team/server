package com.example.modam.domain.user.Presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingRequest {
    @NotBlank(message="닉네임을 입력해주세요.")
    private String nickname;

    @NotNull(message="목표 권수를 입력해주세요.")
    @Min(value=1, message = "목표 권수는 1권 이상이어야 합니다.")
    private Integer goalScore;

    @NotNull(message = "선호 분야를 하나 이상 선택해야 합니다.")
    @NotEmpty(message = "선호 분야 목록은 비어있을 수 없습니다.")
    private List<String> categories;
}
