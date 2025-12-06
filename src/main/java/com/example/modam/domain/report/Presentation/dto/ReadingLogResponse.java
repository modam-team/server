package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;

import java.time.LocalDateTime;

public record ReadingLogResponse(LocalDateTime readAt,
                                 Place readingPlace,
                                 String cover,
                                 String title) {
}
