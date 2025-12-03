package com.example.modam.domain.bookcase.Presentation.dto;

import com.example.modam.domain.bookcase.Domain.BookState;
import lombok.Getter;

@Getter
public class BookCaseSearchRequest {
    private String title;
    private BookState state;
}
