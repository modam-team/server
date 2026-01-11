package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {
    private Place manyPlace;
    private String readingTendency;
}
