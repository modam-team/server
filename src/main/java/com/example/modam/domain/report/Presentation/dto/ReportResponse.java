package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;


@Getter
@Builder
public class ReportResponse {
    private CharacterResponse character;
    private long characterNum;
    private long userTotalNum;
    private ReportBlock<Map<String, Map<String, List<ReportGroup>>>> data;
    private ReportBlock<Map<String, Map<String, List<ReadReportGroup>>>> logData;
}
