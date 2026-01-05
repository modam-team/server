package com.example.modam.domain.book.Presentation.dto;

import java.util.List;

public record BookReviewResponse(long bookId,
                                 ReviewScore reviewScore,
                                 List<String> hashtags) {
}
