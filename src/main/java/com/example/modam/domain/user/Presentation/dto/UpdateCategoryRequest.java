package com.example.modam.domain.user.Presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateCategoryRequest {
    @Schema(description = "선호 카테고리 리스트", example = "[\"소설\", \"여행\", \"경제\"]")
    private List<String> categories;
}