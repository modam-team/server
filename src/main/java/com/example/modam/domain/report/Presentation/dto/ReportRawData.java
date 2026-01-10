package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.report.Domain.Place;

import java.time.LocalDateTime;

public record ReportRawData(
        LocalDateTime finishAt,
        String category,
        String rawHashtags
) {
}
