package com.example.modam.domain.review.Presentation.dto;

import lombok.Getter;

@Getter
public class ChangeCommentRequest {
    private long bookId;
    private String comment;
}
