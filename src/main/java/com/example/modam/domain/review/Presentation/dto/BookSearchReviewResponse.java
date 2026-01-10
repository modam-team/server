package com.example.modam.domain.review.Presentation.dto;

public record BookSearchReviewResponse(long userId, String userName, int rating, String comment, String image) {
}
