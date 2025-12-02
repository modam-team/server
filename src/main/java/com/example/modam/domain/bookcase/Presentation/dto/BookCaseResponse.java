package com.example.modam.domain.bookcase.Presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookCaseResponse {
    private List<BookCaseInfoResponse> before;
    private List<BookCaseInfoResponse> reading;
    private List<BookCaseInfoResponse> after;
}
