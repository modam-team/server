package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;
import lombok.Getter;

@Getter
public class RecordReadingLogRequest {
    private long bookId;
    private Place readingPlace;
}
