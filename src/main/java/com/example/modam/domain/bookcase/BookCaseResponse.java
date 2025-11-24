package com.example.modam.domain.bookcase;

import com.example.modam.domain.book.AladinResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookCaseResponse {
    private List<AladinResponse> before;
    private List<AladinResponse> reading;
    private List<AladinResponse> after;
}
