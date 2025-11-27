package com.example.modam.domain.user.Presentation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameCheckResponse {
    private final boolean isAvailable;
    private final String message;
}
