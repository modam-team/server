package com.example.modam.domain.book.Presentation;

import lombok.Getter;

@Getter
public class BookSearchRequest {
    private String query;
    private String queryType;
}
