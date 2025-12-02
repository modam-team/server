package com.example.modam.domain.user.Presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameCheckResponse {
    private final boolean isAvailable;
    private final String message;
}
