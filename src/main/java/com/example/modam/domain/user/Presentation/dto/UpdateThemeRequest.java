package com.example.modam.domain.user.Presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateThemeRequest {
    @NotBlank
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "올바른 Hex Code 형식이 아닙니다.")
    private String themeColor;
}
