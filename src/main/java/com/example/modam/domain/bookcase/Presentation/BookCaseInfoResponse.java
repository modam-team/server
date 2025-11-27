package com.example.modam.domain.bookcase.Presentation;

import com.example.modam.domain.bookcase.Domain.BookState;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookCaseInfoResponse {
    // BookInfoResponse에서 데이터를 가져옴
    private long bookId;
    private String title;
    private String author;
    private String cover;
    private String categoryName;
    private String publisher;
    private double rate;
    private long totalReview;

    // BookInfoEntity에서 데이터를 가져옴
    private BookState status;
    private LocalDateTime enrollAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    // 완독한 유저만 이거 채움
    private double userRate;
    private String userHashTag;
    private String userComment;
}
