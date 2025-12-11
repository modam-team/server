package com.example.modam.domain.report.Presentation.dto;

import com.example.modam.domain.report.Domain.Place;
import lombok.Getter;

import java.util.List;
import java.util.Map;


@Getter
public class ReportResponse {
    private Place manyPlace;
    private String readingTendency;

    private Map<String, Map<String, List<ReportGroup>>> data;

    public void setData(Map<String, Map<String, List<ReportGroup>>> data) {
        this.data = data;
    }

    public void setCharacter(Place manyPlace, String readingTendency) {
        this.manyPlace = manyPlace;
        this.readingTendency = readingTendency;
    }
}
