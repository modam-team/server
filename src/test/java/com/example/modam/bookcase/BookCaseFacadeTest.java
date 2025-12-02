package com.example.modam.bookcase;

import com.example.modam.domain.book.Application.BookDataService;
import com.example.modam.domain.book.Presentation.dto.BookInfoResponse;
import com.example.modam.domain.bookcase.Application.BookCaseService;
import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Facade.BookCaseFacade;
import com.example.modam.domain.bookcase.Presentation.dto.BookCaseInfoResponse;
import com.example.modam.domain.bookcase.Presentation.dto.BookCaseResponse;
import com.example.modam.domain.review.Application.ReviewService;
import com.example.modam.domain.review.Domain.ReviewEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookCaseFacadeTest {

    @Mock
    private BookDataService bookDataService;

    @Mock
    private BookCaseService bookCaseService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private BookCaseFacade bookCaseFacade;

    @DisplayName("책의 상태를 올바르게 나누는지 확인하는 테스트")
    @Test
    public void book_state_test() {
        BookCaseEntity beforeEntity = mock(BookCaseEntity.class);
        BookCaseEntity readingEntity = mock(BookCaseEntity.class);
        BookCaseEntity afterEntity = mock(BookCaseEntity.class);

        when(beforeEntity.getId()).thenReturn(1L);
        when(readingEntity.getId()).thenReturn(2L);
        when(afterEntity.getId()).thenReturn(3L);

        when(beforeEntity.getStatus()).thenReturn(BookState.BEFORE);
        when(readingEntity.getStatus()).thenReturn(BookState.READING);
        when(afterEntity.getStatus()).thenReturn(BookState.AFTER);

        BookInfoResponse info1 = BookInfoResponse.builder()
                .bookId(101L).title("Title-Before").author("Author-A")
                .cover("cover-url").categoryName("Category")
                .publisher("Publisher").rate(4.5).totalReview(10L)
                .build();

        BookInfoResponse info2 = BookInfoResponse.builder()
                .bookId(102L).title("Title-Reading").author("Author-B")
                .cover("cover-url").categoryName("Category")
                .publisher("Publisher").rate(3.7).totalReview(5L)
                .build();

        BookInfoResponse info3 = BookInfoResponse.builder()
                .bookId(103L).title("Title-After").author("Author-C")
                .cover("cover-url").categoryName("Category")
                .publisher("Publisher").rate(4.0).totalReview(7L)
                .build();

        when(bookDataService.toDto(any())).thenReturn(info1, info2, info3);

        ReviewEntity reviewForBefore = mock(ReviewEntity.class);
        when(reviewForBefore.getRating()).thenReturn(5);

        when(reviewService.getReview(1L)).thenReturn(Optional.of(reviewForBefore));
        when(reviewService.getReview(2L)).thenReturn(Optional.empty());
        when(reviewService.getReview(3L)).thenReturn(Optional.empty());

        List<BookCaseEntity> mockedList = Arrays.asList(beforeEntity, readingEntity, afterEntity);
        when(bookCaseService.getUserBookCase(123L)).thenReturn(mockedList);

        BookCaseResponse response = bookCaseFacade.createBookCaseInfo(123L);

        assertNotNull(response);

        List<BookCaseInfoResponse> before = response.getBefore();
        List<BookCaseInfoResponse> reading = response.getReading();
        List<BookCaseInfoResponse> after = response.getAfter();

        assertEquals(1, before.size());
        assertEquals(1, reading.size());
        assertEquals(1, after.size());

        assertEquals(101L, before.get(0).getBookId());
        assertEquals(102L, reading.get(0).getBookId());
        assertEquals(103L, after.get(0).getBookId());

        assertEquals(5, before.get(0).getUserRate());

        verify(bookDataService, times(3)).toDto(any());
        verify(reviewService).getReview(1L);
        verify(reviewService).getReview(2L);
        verify(reviewService).getReview(3L);
        verify(bookCaseService).getUserBookCase(123L);
    }
}
