package com.example.modam.domain.report.Presentation;

import com.example.modam.domain.report.Domain.Place;
import lombok.Getter;

@Getter
public class RecordReadingLogRequest {
    private long bookCaseId;
    private Place readingPlace;
}
