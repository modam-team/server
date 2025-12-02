package com.example.modam.domain.bookcase.Facade;

import com.example.modam.domain.book.Application.BookDataService;
import com.example.modam.domain.book.Presentation.BookInfoResponse;
import com.example.modam.domain.bookcase.Application.BookCaseService;
import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Presentation.BookCaseInfoResponse;
import com.example.modam.domain.bookcase.Presentation.BookCaseResponse;
import com.example.modam.domain.review.Application.ReviewService;
import com.example.modam.domain.review.Domain.ReviewEntity;
import org.springframework.stereotype.Component;

import java.util.*;

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

        List<BookCaseInfoResponse> before = new ArrayList<>();
        List<BookCaseInfoResponse> reading = new ArrayList<>();
        List<BookCaseInfoResponse> after = new ArrayList<>();

        List<BookCaseEntity> data = bookCaseService.getUserBookCase(userId);
        for (BookCaseEntity b : data) {
            BookInfoResponse info = bookDataService.toDto(b.getBook());
            Optional<ReviewEntity> review = reviewService.getReview(b.getId());
            if (b.getStatus() == BookState.BEFORE) {
                before.add(new BookCaseInfoResponse(info, b, review));
            } else if (b.getStatus() == BookState.READING) {
                reading.add(new BookCaseInfoResponse(info, b, review));
            } else {
                after.add(new BookCaseInfoResponse(info, b, review));
            }
        }

        BookCaseResponse response = new BookCaseResponse(before, reading, after);

        return response;
    }

    public List<BookCaseInfoResponse> searchBookCaseInfo(long userId, String title, BookState state) {
        List<BookCaseEntity> data = bookCaseService.searchUserBookCase(userId, title, state);
        List<BookCaseInfoResponse> response = new ArrayList<>();
        for (BookCaseEntity b : data) {
            BookInfoResponse info = bookDataService.toDto(b.getBook());
            Optional<ReviewEntity> review = reviewService.getReview(b.getId());
            response.add(new BookCaseInfoResponse(info, b, review));
        }

        return response;
    }
}
