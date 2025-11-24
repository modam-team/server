package com.example.modam.domain.bookcase;

import com.example.modam.domain.book.BookEntity;
import com.example.modam.domain.book.AladinResponse;
import com.example.modam.domain.book.BookInfoResponse;
import com.example.modam.domain.book.BookRepository;
import com.example.modam.domain.user.UserEntity;
import com.example.modam.domain.user.UserRepository;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<BookCaseEntity> getUserBook(long userId) {
        List<BookCaseEntity> bookList = bookCaseRepository.findByUser_Id(userId);

        return bookList;
    }

    public BookCaseResponse getUserBookCase(long userId) {
        List<BookCaseEntity> data = getUserBook(userId);

        Map<BookState, List<BookInfoResponse>> grouped = data.stream()
                .collect(Collectors.groupingBy(
                        BookCaseEntity::getStatus,
                        Collectors.mapping(
                                bookCaseEntity -> bookCaseEntity.getBook().toDto(),
                                Collectors.toList()
                        )
                ));

        List<BookInfoResponse> before = grouped.getOrDefault(BookState.BEFORE, Collections.emptyList());
        List<BookInfoResponse> reading = grouped.getOrDefault(BookState.READING, Collections.emptyList());
        List<BookInfoResponse> after = grouped.getOrDefault(BookState.AFTER, Collections.emptyList());

        return new BookCaseResponse(before, reading, after);
    }

    @Transactional
    public BookCaseEntity changeUserBook(long userId, long bookId) {

        Optional<BookCaseEntity> optionalData = bookCaseRepository.findByUser_IdAndBook_Id(userId, bookId);
        if (optionalData.isEmpty()) {
            throw new ApiException(ErrorDefine.BOOKCASE_NOT_FOUND);
        }
        BookCaseEntity data = optionalData.get();

        if (data.getStatus() == BookState.BEFORE) {
            data.setStatus(BookState.READING);
            data.setStartedAt(LocalDateTime.now());
        } else if (data.getStatus() == BookState.READING) {
            data.setStatus(BookState.AFTER);
            data.setFinishedAt(LocalDateTime.now());
        } else {
            throw new ApiException(ErrorDefine.UNAUTHORIZED_STATUS);
        }

        return data;
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
