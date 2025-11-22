package com.example.modam.domain.bookcase;

import com.example.modam.domain.book.BookEntity;
import com.example.modam.domain.book.BookRepository;
import com.example.modam.domain.user.UserEntity;
import com.example.modam.domain.user.UserRepository;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class BookCaseService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookCaseRepository bookCaseRepository;

    public BookCaseService(BookRepository bookRepository, UserRepository userRepository, BookCaseRepository bookCaseRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookCaseRepository = bookCaseRepository;
    }

    public BookEntity getBook(long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(ErrorDefine.BOOK_NOT_FOUND));
    }

    public UserEntity getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));
    }

    @Transactional
    public BookCaseEntity saveUserBook(long userId, long bookId) {

        if (bookCaseRepository.existsByUser_IdAndBook_Id(userId, bookId)) {
            throw new ApiException(ErrorDefine.USER_ALREADY_HAS_BOOK);
        }

        BookEntity book = getBook(bookId);

        UserEntity user = getUser(userId);

        BookCaseEntity userBook = BookCaseEntity.builder()
                .book(book)
                .user(user)
                .status(BookState.BEFORE)
                .enrollAt(LocalDateTime.now())
                .build();

        return bookCaseRepository.save(userBook);
    }
}
