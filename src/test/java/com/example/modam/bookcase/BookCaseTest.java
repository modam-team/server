package com.example.modam.bookcase;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Interface.BookRepository;
import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.bookcase.Application.BookCaseService;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Presentation.dto.BookCaseSaveRequestDTO;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Interface.UserRepository;
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

        UserEntity user = UserEntity.builder()
                .id(userId)
                .name("황록")
                .providerId("12345")
                .isOnboardingCompleted(true)
                .build();
        BookEntity book = new BookEntity(12345, "황록1", "하하하", "황", "소설/문학", "a", "123", true);

        BookCaseSaveRequestDTO dto = mock(BookCaseSaveRequestDTO.class);
        when(dto.getBookId()).thenReturn(bookId);
        when(dto.getState()).thenReturn(BookState.BEFORE);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(bookCaseRepository.save(any(BookCaseEntity.class)))
                .thenAnswer(invocation -> {
                    BookCaseEntity saved = invocation.getArgument(0);
                    return saved;
                });

        BookCaseEntity result = bookCaseService.saveUserBook(userId, dto);

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

        BookCaseSaveRequestDTO dto = mock(BookCaseSaveRequestDTO.class);
        when(dto.getBookId()).thenReturn(bookId);
        when(dto.getState()).thenReturn(BookState.BEFORE);

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> bookCaseService.saveUserBook(userId, dto));
        verify(bookCaseRepository, never()).save(any());
    }

    @DisplayName("유저 없을 때 예외 처리 테스트")
    @Test
    void save_test_userNotFound() {
        long userId = 1L;
        long bookId = 99999L;
        BookEntity book = new BookEntity(bookId, "제목", "설명", "저자", "카테고리", "출판사", "isbn", true);

        BookCaseSaveRequestDTO dto = mock(BookCaseSaveRequestDTO.class);
        when(dto.getBookId()).thenReturn(bookId);
        when(dto.getState()).thenReturn(BookState.BEFORE);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> bookCaseService.saveUserBook(userId, dto));
        verify(bookCaseRepository, never()).save(any());
    }

    @DisplayName("유저의 책장에 이미 책이 있을 때 예외 처리 테스트")
    @Test
    void exception_test_user_already_has_book() {
        long userId = 1L;
        long bookId = 99999L;

        BookCaseSaveRequestDTO dto = mock(BookCaseSaveRequestDTO.class);
        when(dto.getBookId()).thenReturn(bookId);
        when(dto.getState()).thenReturn(BookState.BEFORE);

        when(bookCaseRepository.existsByUser_IdAndBook_Id(userId, bookId)).thenReturn(true);
        assertThrows(ApiException.class, () -> bookCaseService.saveUserBook(userId, dto));

        verify(bookCaseRepository, never()).save(any(BookCaseEntity.class));
        verify(bookRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
    }

    @DisplayName("올바르게 책장 삭제 테스트")
    @Test
    void delete_user_book() {
        long userId = 1L;
        long bookId = 123L;

        BookCaseEntity bookCase = BookCaseEntity.builder()
                .id(10L)
                .build();

        when(bookCaseRepository.findByUser_IdAndBook_Id(userId, bookId))
                .thenReturn(Optional.of(bookCase));

        bookCaseService.deleteUserBook(userId, bookId);

        verify(bookCaseRepository, times(1)).delete(bookCase);
    }

    @DisplayName("책장에 유저와 책이 매칭 안될 때 예외처리")
    @Test
    void delete_user_book_not_found() {
        long userId = 1L;
        long bookId = 123L;

        when(bookCaseRepository.findByUser_IdAndBook_Id(userId, bookId))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> bookCaseService.deleteUserBook(userId, bookId)
        );

        verify(bookCaseRepository, never()).delete(any());
    }


}
