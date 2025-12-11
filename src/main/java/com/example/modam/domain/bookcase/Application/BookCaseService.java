package com.example.modam.domain.bookcase.Application;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Interface.BookRepository;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Presentation.dto.BookCaseSaveRequestDTO;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.utils.VariousFunc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookCaseService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookCaseRepository bookCaseRepository;
    private final VariousFunc variousFunc;

    public BookCaseService(BookRepository bookRepository, UserRepository userRepository,
                           BookCaseRepository bookCaseRepository, VariousFunc variousFunc) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookCaseRepository = bookCaseRepository;
        this.variousFunc = variousFunc;
    }

    public BookEntity getBook(long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(ErrorDefine.BOOK_NOT_FOUND));
    }

    public UserEntity getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));
    }

    public List<BookCaseEntity> getUserBookCase(long userId) {
        List<BookCaseEntity> data = bookCaseRepository.findByUserIdWithBook(userId);

        return data;
    }

    public List<BookCaseEntity> searchUserBookCase(long userId, String title, BookState state) {
        title = variousFunc.toFTS(title);
        return bookCaseRepository.searchByUserAndBookTitle(userId, title, state);
    }

    public List<BookEntity> recommendBook(long userId) {
        String category = userRepository.findUserCategory(userId);
        List<Long> bookCaseIds = bookCaseRepository.findUserBookCaseIds(userId);

        List<BookEntity> recommendBook = bookRepository.recommendByBookCategory(category, bookCaseIds);

        return recommendBook;
    }

    @Transactional
    public void deleteUserBook(long userId, long bookId) {
        Optional<BookCaseEntity> optionalData = bookCaseRepository.findByUser_IdAndBook_Id(userId, bookId);
        if (optionalData.isEmpty()) {
            throw new ApiException(ErrorDefine.BOOKCASE_NOT_FOUND);
        }
        BookCaseEntity data = optionalData.get();

        bookCaseRepository.delete(data);
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
    public BookCaseEntity saveUserBook(long userId, BookCaseSaveRequestDTO dto) {
        long bookId = dto.getBookId();
        BookState state = dto.getState();

        if (bookCaseRepository.existsByUser_IdAndBook_Id(userId, bookId)) {
            throw new ApiException(ErrorDefine.USER_ALREADY_HAS_BOOK);
        }

        BookEntity book = getBook(bookId);

        UserEntity user = getUser(userId);

        if (state == BookState.BEFORE) {
            BookCaseEntity userBook = BookCaseEntity.builder()
                    .book(book)
                    .user(user)
                    .status(BookState.BEFORE)
                    .enrollAt(LocalDateTime.now())
                    .build();

            return bookCaseRepository.save(userBook);
        } else if (state == BookState.READING) {
            BookCaseEntity userBook = BookCaseEntity.builder()
                    .book(book)
                    .user(user)
                    .status(BookState.READING)
                    .enrollAt(LocalDateTime.now())
                    .startedAt(LocalDateTime.now())
                    .build();

            return bookCaseRepository.save(userBook);
        } else {
            BookCaseEntity userBook = BookCaseEntity.builder()
                    .book(book)
                    .user(user)
                    .status(BookState.AFTER)
                    .enrollAt(LocalDateTime.now())
                    .startedAt(LocalDateTime.now())
                    .finishedAt(LocalDateTime.now())
                    .build();

            return bookCaseRepository.save(userBook);
        }

    }
}
