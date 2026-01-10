package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.report.Domain.Place;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReportGroup {
    private LocalDateTime readAt;
    private Place readingPlace;
    private String category;
    private BookState state;
    private List<String> hashtags;
}
