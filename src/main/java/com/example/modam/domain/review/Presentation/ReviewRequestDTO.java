package com.example.modam.domain.review.Presentation;

import lombok.Getter;

@Getter
public class ReviewRequestDTO {
    private long bookCaseId;
    private int rating;
    private String hashtag;
    private String comment;
}
