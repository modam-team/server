package com.example.modam.domain.bookcase.Presentation;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookCaseInfoResponse {
    private long bookId;
    private String title;
    private String author;
    private String cover;
    private String categoryName;
    private String publisher;
    private double rate;
    private long totalReview;
    private LocalDateTime enrollAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private double userRate;
    private String userHashTag;
}
