package com.example.modam.domain.book.Presentation.dto;

import com.example.modam.global.utils.BookSearch.QueryType;
import lombok.Getter;

@Getter
public class BookSearchRequest {
    private String query;
    private QueryType queryType;
}
