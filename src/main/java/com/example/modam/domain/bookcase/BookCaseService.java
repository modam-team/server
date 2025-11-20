package com.example.modam.domain.bookcase;

import com.example.modam.domain.book.BookEntity;
import com.example.modam.domain.book.BookRepository;
import com.example.modam.domain.user.UserEntity;
import com.example.modam.domain.user.UserRepository;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookCaseService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookCaseRepository bookCaseRepository;

    public BookCaseService(BookRepository bookRepository, UserRepository userRepository, BookCaseRepository bookCaseRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookCaseRepository = bookCaseRepository;
    }

    public BookCaseEntity saveUserBook(long userId, long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(ErrorDefine.BOOK_NOT_FOUND));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        BookCaseEntity userBook = BookCaseEntity.builder()
                .book(book)
                .user(user)
                .status(BookState.BEFORE)
                .enrollAt(LocalDateTime.now())
                .build();

        return bookCaseRepository.save(userBook);
    }
}
