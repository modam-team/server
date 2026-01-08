package com.example.modam.domain.report.Presentation.dto;

import java.util.List;

public record ReadingLogResponseWithTheme(
        List<ReadingLogResponse> readingLogResponse,
        String theme
) {
}
