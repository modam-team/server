package com.example.modam.Review;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.review.Application.ReviewService;
import com.example.modam.domain.review.Interface.ReviewRepository;
import com.example.modam.domain.review.Presentation.ReviewRequestDTO;
import com.example.modam.domain.review.Domain.ReviewEntity;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.utils.DefineHashtag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

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

    @DisplayName("책장이 없을 때 예외처리")
    @Test
    void BookCase_NotFound_test() {
        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookCaseId()).thenReturn(999L);

        when(bookCaseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> reviewService.saveReview(1L, dto));
        verify(bookCaseRepository, times(1)).findById(999L);

        verifyNoInteractions(reviewRepository);
    }

    @DisplayName("토큰 주인과 책 주인이 다를 때 예외처리")
    @Test
    void user_NotEqual_test() {
        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookCaseId()).thenReturn(10L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(42L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);

        when(bookCaseRepository.findById(10L)).thenReturn(Optional.of(bookCase));

        assertThrows(ApiException.class, () -> reviewService.saveReview(99L, dto));
        verify(bookCaseRepository, times(1)).findById(10L);
        verifyNoInteractions(reviewRepository);
    }

    @DisplayName("이미 저장한 책장 예외처리")
    @Test
    void Already_save_test() {
        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookCaseId()).thenReturn(10L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(1L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);

        when(bookCaseRepository.findById(10L)).thenReturn(Optional.of(bookCase));
        when(reviewRepository.existsByBookCase_Id(10L)).thenReturn(true);

        assertThrows(ApiException.class, () -> reviewService.saveReview(1L, dto));
        verify(bookCaseRepository, times(1)).findById(10L);
        verify(reviewRepository, times(1)).existsByBookCase_Id(10L);
        verify(reviewRepository, never()).save(any());
    }

    @DisplayName("점수 최대치를 넘을 때 예외처리")
    @Test
    void Exceed_score_test() {
        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookCaseId()).thenReturn(1L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(1L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);

        when(bookCaseRepository.findById(1L)).thenReturn(Optional.of(bookCase));

        when(reviewRepository.existsByBookCase_Id(1L)).thenReturn(false);

        when(dto.getRating()).thenReturn(Integer.MAX_VALUE);

        assertThrows(ApiException.class, () -> reviewService.saveReview(1L, dto));
        verify(bookCaseRepository, times(1)).findById(1L);

        verify(reviewRepository, times(1)).existsByBookCase_Id(1L);
        verify(reviewRepository, never()).save(any());
    }

    @DisplayName("코멘트가 길때 예외처리")
    @Test
    void Exceed_comment_test() {
        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookCaseId()).thenReturn(2L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(2L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);

        when(bookCaseRepository.findById(2L)).thenReturn(Optional.of(bookCase));

        when(reviewRepository.existsByBookCase_Id(2L)).thenReturn(false);

        when(dto.getRating()).thenReturn(1);

        String longComment = "a".repeat(5000);
        when(dto.getComment()).thenReturn(longComment);

        assertThrows(ApiException.class, () -> reviewService.saveReview(2L, dto));
        verify(bookCaseRepository, times(1)).findById(2L);

        verify(reviewRepository, times(1)).existsByBookCase_Id(2L);
        verify(reviewRepository, never()).save(any());
    }

    @DisplayName("정해진 해시태그가 아닐때 예외처리")
    @Test
    void Invalid_hashtag_test() {
        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookCaseId()).thenReturn(3L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(3L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);

        when(bookCaseRepository.findById(3L)).thenReturn(Optional.of(bookCase));

        when(reviewRepository.existsByBookCase_Id(3L)).thenReturn(false);

        when(dto.getRating()).thenReturn(3);
        when(dto.getComment()).thenReturn("ok");
        when(dto.getHashtag()).thenReturn("not-a-hashtag");

        when(defineHashtag.isHashtag("not-a-hashtag")).thenReturn(false);

        assertThrows(ApiException.class, () -> reviewService.saveReview(3L, dto));
        verify(bookCaseRepository, times(1)).findById(3L);
        verify(reviewRepository, times(1)).existsByBookCase_Id(3L);
        verify(defineHashtag, times(1)).isHashtag("not-a-hashtag");
        verify(reviewRepository, never()).save(any());
    }

    @DisplayName("올바르게 리뷰 저장 테스트")
    @Test
    void SaveReview_test() {
        ReviewRequestDTO dto = mock(ReviewRequestDTO.class);
        when(dto.getBookCaseId()).thenReturn(5L);

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(5L);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);
        when(bookCase.getUser()).thenReturn(owner);

        when(bookCaseRepository.findById(5L)).thenReturn(Optional.of(bookCase));
        when(reviewRepository.existsByBookCase_Id(5L)).thenReturn(false);

        when(dto.getRating()).thenReturn(4);
        when(dto.getComment()).thenReturn("좋은 책이었어요");
        when(dto.getHashtag()).thenReturn("웃긴");
        when(defineHashtag.isHashtag("웃긴")).thenReturn(true);

        ArgumentCaptor<ReviewEntity> captor = ArgumentCaptor.forClass(ReviewEntity.class);
        when(reviewRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewEntity saved = reviewService.saveReview(5L, dto);

        assertNotNull(saved);
        assertEquals("좋은 책이었어요", saved.getComment());
        assertEquals("웃긴", saved.getHashtag());
        assertEquals(4, saved.getRating());
        assertSame(bookCase, saved.getBookCase());

        verify(bookCaseRepository, times(1)).findById(5L);
        verify(reviewRepository, times(1)).existsByBookCase_Id(5L);
        verify(defineHashtag, times(1)).isHashtag("웃긴");
        verify(reviewRepository, times(1)).save(any(ReviewEntity.class));
    }
}
