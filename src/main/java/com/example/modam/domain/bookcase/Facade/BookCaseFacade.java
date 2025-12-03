package com.example.modam.domain.bookcase.Facade;

import com.example.modam.domain.book.Application.BookDataService;
import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Presentation.dto.BookInfoResponse;
import com.example.modam.domain.book.Presentation.dto.ReviewScore;
import com.example.modam.domain.bookcase.Application.BookCaseService;
import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Presentation.dto.BookCaseInfoResponse;
import com.example.modam.domain.bookcase.Presentation.dto.BookCaseResponse;
import com.example.modam.domain.review.Application.ReviewService;
import com.example.modam.domain.review.Domain.ReviewEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BookCaseFacade {

    private BookDataService bookDataService;
    private BookCaseService bookCaseService;
    private ReviewService reviewService;

    public BookCaseFacade(BookDataService bookDataService, BookCaseService bookCaseService, ReviewService reviewService) {
        this.bookCaseService = bookCaseService;
        this.bookDataService = bookDataService;
        this.reviewService = reviewService;
    }

    public BookCaseResponse createBookCaseInfo(long userId) {
        List<BookCaseEntity> bookCases = bookCaseService.getUserBookCase(userId);
        if (bookCases.isEmpty()) {
            return new BookCaseResponse(List.of(), List.of(), List.of());
        }

        List<Long> bookIds = bookCases.stream()
                .map(bc -> bc.getBook().getId())
                .distinct()
                .toList();

        List<ReviewScore> scores = bookDataService.getBookReviewScore(bookIds);
        Map<Long, ReviewScore> scoreMap = scores.stream()
                .collect(Collectors.toMap(ReviewScore::BookId, Function.identity()));

        List<Long> caseIds = bookCases.stream().map(BookCaseEntity::getId).toList();
        List<ReviewEntity> reviews = reviewService.getByBookCaseIds(caseIds);
        Map<Long, ReviewEntity> reviewMap = reviews.stream()
                .collect(Collectors.toMap(r -> r.getBookCase().getId(), Function.identity()));

        List<BookCaseInfoResponse> before = new ArrayList<>();
        List<BookCaseInfoResponse> reading = new ArrayList<>();
        List<BookCaseInfoResponse> after = new ArrayList<>();

        for (BookCaseEntity bc : bookCases) {
            BookEntity book = bc.getBook();
            ReviewScore score = scoreMap.get(book.getId());
            BookInfoResponse bookInfo = bookDataService.toDto(book, score);

            Optional<ReviewEntity> reviewOpt = Optional.ofNullable(reviewMap.get(bc.getId()));
            BookCaseInfoResponse dto = new BookCaseInfoResponse(bookInfo, bc, reviewOpt);

            if (bc.getStatus() == BookState.BEFORE) {
                before.add(dto);
            } else if (bc.getStatus() == BookState.READING) {
                reading.add(dto);
            } else {
                after.add(dto);
            }
        }

        return new BookCaseResponse(before, reading, after);
    }

    public List<BookCaseInfoResponse> searchBookCaseInfo(long userId, String title, BookState state) {
        List<BookCaseEntity> bookCases = bookCaseService.searchUserBookCase(userId, title, state);
        List<BookCaseInfoResponse> response = new ArrayList<>();

        if (bookCases.isEmpty()) {
            return response;
        }

        List<Long> bookIds = bookCases.stream()
                .map(bc -> bc.getBook().getId())
                .distinct()
                .toList();

        List<ReviewScore> scores = bookDataService.getBookReviewScore(bookIds);
        Map<Long, ReviewScore> scoreMap = scores.stream()
                .collect(Collectors.toMap(ReviewScore::BookId, Function.identity()));

        List<Long> caseIds = bookCases.stream().map(BookCaseEntity::getId).toList();
        List<ReviewEntity> reviews = reviewService.getByBookCaseIds(caseIds);
        Map<Long, ReviewEntity> reviewMap = reviews.stream()
                .collect(Collectors.toMap(r -> r.getBookCase().getId(), Function.identity()));

        for (BookCaseEntity bc : bookCases) {
            BookEntity book = bc.getBook();
            ReviewScore score = scoreMap.get(book.getId());
            BookInfoResponse bookInfo = bookDataService.toDto(book, score);

            Optional<ReviewEntity> reviewOpt = Optional.ofNullable(reviewMap.get(bc.getId()));
            BookCaseInfoResponse dto = new BookCaseInfoResponse(bookInfo, bc, reviewOpt);

            response.add(dto);
        }

        return response;
    }
}
