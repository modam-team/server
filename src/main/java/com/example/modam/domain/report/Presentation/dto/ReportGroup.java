package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReportGroup {
    private LocalDateTime readAt;
    private Place readingPlace;
    private String category;
    private List<String> hashtags;
}
