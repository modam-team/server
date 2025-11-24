package com.example.modam.domain.book;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookInfoResponse {
    private long bookId;
    private String title;
    private String author;
    private String cover;
    private String categoryName;
    private String publisher;
}
