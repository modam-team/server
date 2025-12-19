package com.example.modam.domain.review.Presentation.dto;

import java.util.List;

public record ReviewResponse(int rating, String comment, List<String> hashtag) {
}
