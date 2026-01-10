package com.example.modam.domain.report.Presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportBlock<T> {
    private String code;
    private T Data;
}
