package com.example.modam.domain.review.Application;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.review.Domain.HashtagEntity;
import com.example.modam.domain.review.Domain.ReviewEntity;
import com.example.modam.domain.review.Interface.ReviewRepository;
import com.example.modam.domain.review.Presentation.dto.ReviewRequestDTO;
import com.example.modam.global.utils.DefineHashtag;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final BookCaseRepository bookCaseRepository;
    private final ReviewRepository reviewRepository;
    private final DefineHashtag defineHashtag;

    private final int COMMENT_MAX_LEN = 1000;
    private final int RATING_MAX_NUM = 5;

    public ReviewService(BookCaseRepository bookCaseRepository, ReviewRepository reviewRepository, DefineHashtag defineHashtag) {
        this.bookCaseRepository = bookCaseRepository;
        this.reviewRepository = reviewRepository;
        this.defineHashtag = defineHashtag;
    }

    public List<ReviewEntity> getByBookCaseIds(List<Long> caseIds) {

        if (caseIds == null || caseIds.isEmpty()) {
            return List.of();
        }
        return reviewRepository.findByBookCaseIds(caseIds);
    }

    @Transactional
    public ReviewEntity saveReview(long userId, ReviewRequestDTO dto) {

        log.info("[review save request] userId={}, review bookId={}, review rating={}, review comment={}",
                userId, dto.getBookId(), dto.getRating(), dto.getComment());

        Optional<BookCaseEntity> findBook = bookCaseRepository.findUserBookCaseId(userId, dto.getBookId());
        if (findBook.isEmpty()) {
            throw new ApiException(ErrorDefine.BOOKCASE_NOT_FOUND);
        }

        BookCaseEntity book = findBook.get();
        if (userId != book.getUser().getId()) {
            throw new ApiException(ErrorDefine.UNAUTHORIZED_USER);
        }

        if (book.getStatus() != BookState.AFTER) {
            throw new ApiException(ErrorDefine.UNAUTHORIZED_STATUS);
        }

        if (reviewRepository.existsByBookCase_Id(book.getId())) {
            throw new ApiException(ErrorDefine.REVIEW_ALREADY_EXISTS);
        }

        if (dto.getRating() > RATING_MAX_NUM) {
            throw new ApiException(ErrorDefine.EXCEED_MAX_RATING_NUM);
        }

        if (dto.getComment().length() > COMMENT_MAX_LEN) {
            throw new ApiException(ErrorDefine.EXCEED_MAX_COMMENT_LENGTH);
        }

        if (dto.getHashtag().isEmpty() || dto.getHashtag().size() > 3) {
            throw new ApiException(ErrorDefine.INVALID_HASHTAG);
        }

        for (String s : dto.getHashtag()) {
            if (!defineHashtag.isHashtag(s)) {
                throw new ApiException(ErrorDefine.INVALID_HASHTAG);
            }
        }

        ReviewEntity review = ReviewEntity.builder()
                .bookCase(book)
                .comment(dto.getComment())
                .rating(dto.getRating())
                .build();

        List<HashtagEntity> hashtags = dto.getHashtag().stream()
                .map(tag -> HashtagEntity.builder()
                        .tag(tag)
                        .review(review)
                        .build()
                )
                .toList();

        review.getHashtags().addAll(hashtags);

        return reviewRepository.save(review);
    }
}
