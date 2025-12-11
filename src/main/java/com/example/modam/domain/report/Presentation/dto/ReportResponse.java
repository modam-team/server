package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;

import java.time.LocalDateTime;
import java.util.List;

public record ReportResponse(
        LocalDateTime readAt,
        Place readingPlace,
        String category,
        String rawHashtags
) {
}
