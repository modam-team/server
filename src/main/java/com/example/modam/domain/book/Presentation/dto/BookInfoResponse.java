package com.example.modam.domain.book.Presentation.dto;

import com.example.modam.domain.review.Domain.HashtagEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
    private String link;
    private List<String> hashtags;
}
