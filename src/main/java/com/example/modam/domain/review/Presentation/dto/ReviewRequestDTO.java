package com.example.modam.domain.review.Presentation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewRequestDTO {
    private long bookId;
    private int rating;
    private List<String> hashtag;
    private String comment;
}
