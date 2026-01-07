package com.example.modam.Review;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.review.Application.ReviewService;
import com.example.modam.domain.review.Interface.ReviewRepository;
import com.example.modam.domain.review.Presentation.dto.ChangeCommentRequest;
import com.example.modam.domain.review.Presentation.dto.ReviewRequestDTO;
import com.example.modam.domain.review.Domain.ReviewEntity;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.utils.DefineHashtag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private BookCaseRepository bookCaseRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private DefineHashtag defineHashtag;

    @InjectMocks
    private ReviewService reviewService;

    @DisplayName("책장이 없을 때 BOOKCASE_NOT_FOUND 예외")
    @Test
    void BookCase_NotFound_test() {

        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookId()).thenReturn(999L);

        when(bookCaseRepository.findUserBookCaseId(1L, 999L))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> reviewService.saveReview(1L, dto));

        verify(bookCaseRepository, times(1)).findUserBookCaseId(1L, 999L);
        verifyNoInteractions(reviewRepository);
    }

    @DisplayName("책장 주인과 로그인 유저가 다르면 UNAUTHORIZED_USER 예외")
    @Test
    void user_NotEqual_test() {

        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookId()).thenReturn(10L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(42L); // 다른 사용자

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);

        when(bookCaseRepository.findUserBookCaseId(99L, 10L))
                .thenReturn(Optional.of(bookCase));

        assertThrows(ApiException.class, () -> reviewService.saveReview(99L, dto));

        verify(bookCaseRepository, times(1)).findUserBookCaseId(99L, 10L);
        verifyNoInteractions(reviewRepository);
    }

    @DisplayName("리뷰가 이미 존재하면 REVIEW_ALREADY_EXISTS 예외")
    @Test
    void Already_save_test() {

        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookId()).thenReturn(10L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(1L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);
        when(bookCase.getStatus()).thenReturn(BookState.AFTER);
        when(bookCase.getId()).thenReturn(10L);

        when(bookCaseRepository.findUserBookCaseId(1L, 10L))
                .thenReturn(Optional.of(bookCase));

        when(reviewRepository.existsByBookCase_Id(10L))
                .thenReturn(true);

        assertThrows(ApiException.class, () -> reviewService.saveReview(1L, dto));

        verify(bookCaseRepository, times(1)).findUserBookCaseId(1L, 10L);
        verify(reviewRepository, times(1)).existsByBookCase_Id(10L);
        verify(reviewRepository, never()).save(any());
    }

    @DisplayName("점수 초과 시 EXCEED_MAX_RATING_NUM 예외")
    @Test
    void Exceed_score_test() {

        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookId()).thenReturn(1L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(1L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);
        when(bookCase.getStatus()).thenReturn(BookState.AFTER);
        when(bookCase.getId()).thenReturn(1L);

        when(bookCaseRepository.findUserBookCaseId(1L, 1L))
                .thenReturn(Optional.of(bookCase));

        when(reviewRepository.existsByBookCase_Id(1L))
                .thenReturn(false);

        when(dto.getRating()).thenReturn(Integer.MAX_VALUE);

        assertThrows(ApiException.class, () -> reviewService.saveReview(1L, dto));

        verify(bookCaseRepository, times(1)).findUserBookCaseId(1L, 1L);
        verify(reviewRepository, times(1)).existsByBookCase_Id(1L);
        verify(reviewRepository, never()).save(any());
    }

    @DisplayName("코멘트 길이 초과 시 예외")
    @Test
    void Exceed_comment_test() {

        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookId()).thenReturn(2L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(2L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);
        when(bookCase.getStatus()).thenReturn(BookState.AFTER);
        when(bookCase.getId()).thenReturn(2L);

        when(bookCaseRepository.findUserBookCaseId(2L, 2L))
                .thenReturn(Optional.of(bookCase));

        when(reviewRepository.existsByBookCase_Id(2L))
                .thenReturn(false);

        when(dto.getRating()).thenReturn(3);

        String longComment = "a".repeat(5000);
        when(dto.getComment()).thenReturn(longComment);

        assertThrows(ApiException.class, () -> reviewService.saveReview(2L, dto));

        verify(bookCaseRepository, times(1)).findUserBookCaseId(2L, 2L);
        verify(reviewRepository, times(1)).existsByBookCase_Id(2L);
        verify(reviewRepository, never()).save(any());
    }

    @DisplayName("해시태그가 유효하지 않으면 예외")
    @Test
    void Invalid_hashtag_test() {

        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookId()).thenReturn(3L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(3L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);
        when(bookCase.getStatus()).thenReturn(BookState.AFTER);
        when(bookCase.getId()).thenReturn(3L);

        when(bookCaseRepository.findUserBookCaseId(3L, 3L))
                .thenReturn(Optional.of(bookCase));

        when(reviewRepository.existsByBookCase_Id(3L))
                .thenReturn(false);

        when(dto.getRating()).thenReturn(3);
        when(dto.getComment()).thenReturn("ok");

        when(dto.getHashtag()).thenReturn(List.of("invalid-tag"));
        when(defineHashtag.isHashtag("invalid-tag")).thenReturn(false);

        assertThrows(ApiException.class, () -> reviewService.saveReview(3L, dto));

        verify(bookCaseRepository, times(1)).findUserBookCaseId(3L, 3L);
        verify(reviewRepository, times(1)).existsByBookCase_Id(3L);
        verify(defineHashtag, times(1)).isHashtag("invalid-tag");
        verify(reviewRepository, never()).save(any());
    }

    @DisplayName("정상 저장")
    @Test
    void SaveReview_test() {

        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookId()).thenReturn(5L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(5L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);
        when(bookCase.getStatus()).thenReturn(BookState.AFTER);
        when(bookCase.getId()).thenReturn(5L);

        when(bookCaseRepository.findUserBookCaseId(5L, 5L))
                .thenReturn(Optional.of(bookCase));

        when(reviewRepository.existsByBookCase_Id(5L))
                .thenReturn(false);

        when(dto.getRating()).thenReturn(4);
        when(dto.getComment()).thenReturn("좋은 책이었어요");
        when(dto.getHashtag()).thenReturn(List.of("웃긴", "따뜻한"));

        when(defineHashtag.isHashtag("웃긴")).thenReturn(true);
        when(defineHashtag.isHashtag("따뜻한")).thenReturn(true);

        ArgumentCaptor<ReviewEntity> captor = ArgumentCaptor.forClass(ReviewEntity.class);
        when(reviewRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewEntity saved = reviewService.saveReview(5L, dto);

        assertNotNull(saved);
        assertEquals("좋은 책이었어요", saved.getComment());
        assertEquals(4, saved.getRating());
        assertSame(bookCase, saved.getBookCase());

        assertEquals(2, saved.getHashtags().size());
        assertEquals("웃긴", saved.getHashtags().get(0).getTag());
        assertEquals("따뜻한", saved.getHashtags().get(1).getTag());

        verify(bookCaseRepository, times(1)).findUserBookCaseId(5L, 5L);
        verify(reviewRepository, times(1)).existsByBookCase_Id(5L);
        verify(reviewRepository, times(1)).save(any(ReviewEntity.class));
    }

    @DisplayName("리뷰 코멘트가 blank면 INVALID_HEADER_ERROR 예외")
    @Test
    void change_comment_invalid_test() {

        ChangeCommentRequest dto = mock(ChangeCommentRequest.class);
        when(dto.getComment()).thenReturn("   "); // blank

        ApiException exception = assertThrows(
                ApiException.class,
                () -> reviewService.changeReviewComment(1L, dto)
        );

        assertEquals(ErrorDefine.INVALID_HEADER_ERROR, exception.getError());

        verify(reviewRepository, never())
                .findByBookIdAndUserId(anyLong(), anyLong());
    }

    @DisplayName("리뷰 코멘트 길이 초과 시 EXCEED_MAX_COMMENT_LENGTH 예외")
    @Test
    void change_comment_length_exceed_test() {

        ChangeCommentRequest dto = mock(ChangeCommentRequest.class);
        when(dto.getComment()).thenReturn("a".repeat(1001));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> reviewService.changeReviewComment(1L, dto)
        );

        assertEquals(ErrorDefine.EXCEED_MAX_COMMENT_LENGTH, exception.getError());

        verify(reviewRepository, never())
                .findByBookIdAndUserId(anyLong(), anyLong());
    }



    @DisplayName("리뷰 코멘트 정상 수정")
    @Test
    void change_comment_success_test() {

        ChangeCommentRequest dto = mock(ChangeCommentRequest.class);
        when(dto.getBookId()).thenReturn(10L);
        when(dto.getComment()).thenReturn("사실 이 책은 재밌어요");

        ReviewEntity review = mock(ReviewEntity.class);
        when(reviewRepository.findByBookIdAndUserId(10L, 1L))
                .thenReturn(review);

        ReviewEntity result = reviewService.changeReviewComment(1L, dto);

        assertNotNull(result);
        assertSame(review, result);

        verify(reviewRepository, times(1))
                .findByBookIdAndUserId(10L, 1L);
        verify(review, times(1))
                .setComment("사실 이 책은 재밌어요");
    }

}
