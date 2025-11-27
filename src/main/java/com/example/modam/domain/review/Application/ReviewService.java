package com.example.modam.domain.review.Application;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.review.Domain.ReviewEntity;
import com.example.modam.domain.review.Interface.ReviewRepository;
import com.example.modam.domain.review.Presentation.ReviewRequestDTO;
import com.example.modam.global.utils.DefineHashtag;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    public Optional<ReviewEntity> getReview(long bookCaseId) {
        return reviewRepository.findByBookCase_Id(bookCaseId);
    }

    @Transactional
    public ReviewEntity saveReview(long userId, ReviewRequestDTO dto) {
        Optional<BookCaseEntity> findBook = bookCaseRepository.findById(dto.getBookCaseId());
        if (findBook.isEmpty()) {
            throw new ApiException(ErrorDefine.BOOKCASE_NOT_FOUND);
        }

        BookCaseEntity book = findBook.get();
        if (userId != book.getUser().getId()) {
            throw new ApiException(ErrorDefine.UNAUTHORIZED_USER);
        }

        if (reviewRepository.existsByBookCase_Id(dto.getBookCaseId())) {
            throw new ApiException(ErrorDefine.REVIEW_ALREADY_EXISTS);
        }

        if (dto.getRating() > RATING_MAX_NUM) {
            throw new ApiException(ErrorDefine.EXCEED_MAX_RATING_NUM);
        }

        if (dto.getComment().length() > COMMENT_MAX_LEN) {
            throw new ApiException(ErrorDefine.EXCEED_MAX_COMMENT_LENGTH);
        }

        if (!defineHashtag.isHashtag(dto.getHashtag())) {
            throw new ApiException(ErrorDefine.INVALID_HASHTAG);
        }

        ReviewEntity review = ReviewEntity.builder()
                .bookCase(book)
                .comment(dto.getComment())
                .hashtag(dto.getHashtag())
                .rating(dto.getRating())
                .build();

        return reviewRepository.save(review);
    }
}
