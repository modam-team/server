package com.example.modam.bookcase;

import com.example.modam.domain.book.Application.BookDataService;
import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Presentation.dto.BookInfoResponse;
import com.example.modam.domain.book.Presentation.dto.ReviewScore;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.nullable;

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

    @DisplayName("책의 상태를 올바르게 나누는지 확인하는 테스트 (수정: getByBookCaseIds + toDto(book, score))")
    @Test
    public void book_state_test() {
        BookCaseEntity beforeEntity = mock(BookCaseEntity.class);
        BookCaseEntity readingEntity = mock(BookCaseEntity.class);
        BookCaseEntity afterEntity = mock(BookCaseEntity.class);

        doReturn(1L).when(beforeEntity).getId();
        doReturn(2L).when(readingEntity).getId();
        doReturn(3L).when(afterEntity).getId();

        doReturn(BookState.BEFORE).when(beforeEntity).getStatus();
        doReturn(BookState.READING).when(readingEntity).getStatus();
        doReturn(BookState.AFTER).when(afterEntity).getStatus();

        BookEntity book1 = mock(BookEntity.class);
        BookEntity book2 = mock(BookEntity.class);
        BookEntity book3 = mock(BookEntity.class);
        doReturn(101L).when(book1).getId();
        doReturn(102L).when(book2).getId();
        doReturn(103L).when(book3).getId();

        doReturn(book1).when(beforeEntity).getBook();
        doReturn(book2).when(readingEntity).getBook();
        doReturn(book3).when(afterEntity).getBook();

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

        when(bookDataService.toDto(any(BookEntity.class), nullable(ReviewScore.class)))
                .thenReturn(info1, info2, info3);

        ReviewEntity reviewForBefore = mock(ReviewEntity.class);
        doReturn(5).when(reviewForBefore).getRating();
        doReturn(beforeEntity).when(reviewForBefore).getBookCase();
        when(reviewService.getByBookCaseIds(Arrays.asList(1L, 2L, 3L)))
                .thenReturn(Arrays.asList(reviewForBefore));

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

        verify(bookDataService, times(3)).toDto(any(BookEntity.class), nullable(ReviewScore.class));
        verify(reviewService).getByBookCaseIds(Arrays.asList(1L, 2L, 3L));
        verify(bookCaseService).getUserBookCase(123L);
    }
}
