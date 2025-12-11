package com.example.modam.domain.report.Presentation.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ReportResponse {
    private Map<String, Map<String, List<ReportGroup>>> data;
}
