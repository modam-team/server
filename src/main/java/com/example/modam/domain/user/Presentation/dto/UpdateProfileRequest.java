package com.example.modam.domain.user.Presentation.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "프로필 정보 수정 요청 DTO")
public class UpdateProfileRequest {

    @NotBlank(message="닉네임은 필수입니다.")
    @Size(min=1, max=30, message = "닉네임은 1자 이상 30자 이하입니다.")
    private String nickname;

    @NotNull(message = "공개 여부 설정은 필수입니다.")
    private Boolean isPublic;

    // id는 어떤 id인지 몰라서 1206회의 이후 추가할 예정
}
