package com.example.modam.domain.user.Presentation.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "프로필 정보 수정 요청 DTO")
public class UpdateProfileRequest {

    @Size(min=1, max=30, message = "닉네임은 1자 이상 30자 이하입니다.")
    private String nickname;

    private Boolean isPublic;

    @Min(value = 1, message = "목표 권수는 1권 이상이어야 합니다.")
    @Max(value = 1000, message = "목표 권수는 1000권 이하로 설정해주세요.")
    private Integer goalScore;
}
