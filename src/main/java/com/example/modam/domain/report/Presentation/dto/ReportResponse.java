package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;


@Getter
@Builder
public class ReportResponse {
    private Place manyPlace;
    private String readingTendency;
    private long characterNum;
    private long userTotalNum;
    private Map<String, Map<String, List<ReportGroup>>> data;
}
