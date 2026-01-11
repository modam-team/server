package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CharacterResponse {
    private Place manyPlace;
    private String readingTendency;
}
