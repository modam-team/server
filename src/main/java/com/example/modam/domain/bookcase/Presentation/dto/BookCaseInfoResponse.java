package com.example.modam.domain.bookcase.Presentation.dto;

import com.example.modam.domain.book.Presentation.dto.BookInfoResponse;
import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.review.Domain.ReviewEntity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private int userRate;
    private List<String> userHashTag;
    private String userComment;

    public BookCaseInfoResponse(BookInfoResponse bookInfo, BookCaseEntity bookCaseEntity, Optional<ReviewEntity> reviewEntity) {
        this.bookId = bookInfo.getBookId();
        this.title = bookInfo.getTitle();
        this.author = bookInfo.getAuthor();
        this.cover = bookInfo.getCover();
        this.categoryName = bookInfo.getCategoryName();
        this.publisher = bookInfo.getPublisher();
        this.rate = bookInfo.getRate();
        this.totalReview = bookInfo.getTotalReview();
        this.status = bookCaseEntity.getStatus();
        this.enrollAt = bookCaseEntity.getEnrollAt();
        this.startedAt = bookCaseEntity.getStartedAt();
        this.finishedAt = bookCaseEntity.getFinishedAt();
        this.userRate = reviewEntity.map(ReviewEntity::getRating).orElse(0);
        this.userHashTag = reviewEntity
                .map(r -> r.getHashtags().stream()
                        .map(h -> h.getTag())
                        .toList())
                .orElse(null);
        this.userComment = reviewEntity.map(ReviewEntity::getComment).orElse(null);
    }
}
