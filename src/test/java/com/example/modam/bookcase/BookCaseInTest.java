package com.example.modam.bookcase;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.bookcase.Application.BookCaseService;
import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.bookcase.Presentation.BookCaseResponse;
import com.example.modam.domain.user.UserEntity;
import com.example.modam.global.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookCaseInTest {

    @Mock
    private BookCaseRepository bookCaseRepository;


    @InjectMocks
    private BookCaseService bookCaseService;

    private BookEntity makeBook(long id, String title) {
        return BookEntity.builder()
                .id(id)
                .title(title)
                .author("author-" + id)
                .publisher("publisher-" + id)
                .categoryName("category-" + id)
                .cover("cover-" + id)
                .itemId("item-" + id)
                .build();
    }

    private BookCaseEntity makeBookCase(long id, UserEntity user, BookEntity book, BookState state) {
        return BookCaseEntity.builder()
                .id(id)
                .user(user)
                .book(book)
                .status(state)
                .build();
    }


    @DisplayName("책 상태를 올바르게 바꾸는지 확인하는 테스트")
    @Test
    void status_change_test() {
        long userId = 42L;
        UserEntity user = UserEntity.builder().id(userId).name("tester").build();
        BookEntity book = makeBook(1L, "before-book");
        BookCaseEntity bc = makeBookCase(1L, user, book, BookState.BEFORE);

        when(bookCaseRepository.findByUser_IdAndBook_Id(userId, 1L))
                .thenReturn(Optional.of(bc));

        BookCaseEntity result = bookCaseService.changeUserBook(userId, 1L);

        assertEquals(BookState.READING, result.getStatus());
    }

    @DisplayName("후에서 예외처리 테스트")
    @Test
    void status_not_change_after() {
        long userId = 42L;
        UserEntity user = UserEntity.builder().id(userId).name("tester").build();
        BookEntity book = makeBook(1L, "after-book");
        BookCaseEntity bc = makeBookCase(1L, user, book, BookState.AFTER);

        when(bookCaseRepository.findByUser_IdAndBook_Id(userId, 1L))
                .thenReturn(Optional.of(bc));

        assertThrows(ApiException.class, () ->
                bookCaseService.changeUserBook(userId, 1L)
        );
    }

    @DisplayName("시간 변경 테스트")
    @Test
    void change_time() {
        long userId = 42L;
        UserEntity user = UserEntity.builder().id(userId).name("tester").build();
        BookEntity book = makeBook(1L, "before-book");
        BookCaseEntity bc = makeBookCase(1L, user, book, BookState.BEFORE);

        when(bookCaseRepository.findByUser_IdAndBook_Id(userId, 1L))
                .thenReturn(Optional.of(bc));

        BookCaseEntity result = bookCaseService.changeUserBook(userId, 1L);

        assertNotNull(result.getStartedAt(), "before에서 시작하면 startedAt이 있어야함");
    }
}
