package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;

import java.time.LocalDateTime;
import java.util.List;

public record ReportResponse(LocalDateTime readAt,
                             Place readingPlace,
                             String category,
                             // 해시태그들이 ' '을 띄고 concat되있음
                             String rawHashtags) {
    public List<String> splitHashtags() {
        if (rawHashtags == null || rawHashtags.isBlank()) {
            return List.of();
        }
        return List.of(rawHashtags.split(" "));
    }
}
