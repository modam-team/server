package com.example.modam.domain.book.Presentation.dto;

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
    private double rate;
    private long totalReview;
}
