package com.example.modam.bookcase;

import com.example.modam.domain.book.BookEntity;
import com.example.modam.domain.book.BookRepository;
import com.example.modam.domain.bookcase.BookCaseEntity;
import com.example.modam.domain.bookcase.BookCaseRepository;
import com.example.modam.domain.bookcase.BookCaseService;
import com.example.modam.domain.bookcase.BookState;
import com.example.modam.domain.user.UserEntity;
import com.example.modam.domain.user.UserRepository;
import com.example.modam.global.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookCaseTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookCaseRepository bookCaseRepository;

    @InjectMocks
    private BookCaseService bookCaseService;

    @DisplayName("책을 저장할 때 상태와 참조 데이터를 확인하는 테스트")
    @Test
    void save_test_success() {
        long userId = 1L;
        long bookId = 12345L;

        UserEntity user = new UserEntity(1, "황록", "a1234@naver.com");
        BookEntity book = new BookEntity(12345, "황록1", "하하하", "황", "소설/문학", "a", "123");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(bookCaseRepository.save(any(BookCaseEntity.class)))
                .thenAnswer(invocation -> {
                    BookCaseEntity saved = invocation.getArgument(0);
                    return saved;
                });

        BookCaseEntity result = bookCaseService.saveUserBook(userId, bookId);

        ArgumentCaptor<BookCaseEntity> captor = ArgumentCaptor.forClass(BookCaseEntity.class);
        verify(bookCaseRepository, times(1)).save(captor.capture());
        BookCaseEntity captured = captor.getValue();

        assertNotNull(result);
        assertEquals(BookState.BEFORE, captured.getStatus(), "상태가 BEFORE여야 함");
        assertSame(book, captured.getBook(), "참조된 book이 같아야 함");
        assertSame(user, captured.getUser(), "참조된 user가 같아야 함");
        assertNotNull(captured.getEnrollAt(), "enrollAt이 null이면 안 됨");
    }

    @DisplayName("책 없을 때 예외 처리 테스트")
    @Test
    void save_test_bookNotFound() {
        long userId = 1L;
        long bookId = 99999L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> bookCaseService.saveUserBook(userId, bookId));
        verify(bookCaseRepository, never()).save(any());
    }

    @DisplayName("유저 없을 때 예외 처리 테스트")
    @Test
    void save_test_userNotFound(){
        long userId = 1L;
        long bookId = 99999L;
        BookEntity book = new BookEntity(bookId, "제목", "설명", "저자", "카테고리", "출판사", "isbn");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> bookCaseService.saveUserBook(userId, bookId));
        verify(bookCaseRepository, never()).save(any());
    }
}
