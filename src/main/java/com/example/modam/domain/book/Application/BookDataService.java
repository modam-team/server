package com.example.modam.domain.book.Application;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Interface.BookRepository;
import com.example.modam.domain.book.Presentation.dto.AladinResponse;
import com.example.modam.domain.book.Presentation.dto.BookInfoResponse;
import com.example.modam.domain.book.Presentation.dto.ReviewScore;
import com.example.modam.domain.book.Presentation.dto.addBookRequest;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.utils.CategoryMapping;
import com.example.modam.global.utils.VariousFunc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookDataService {

    private final BookRepository bookRepository;
    private final VariousFunc variousFunc;

    public BookDataService(BookRepository bookRepository, VariousFunc variousFunc) {
        this.bookRepository = bookRepository;
        this.variousFunc = variousFunc;
    }

    public BookInfoResponse toDto(BookEntity book, ReviewScore score) {
        long count = 0;
        double rate = 0;
        if (score != null && score.reviewCount() > 0) {
            count = score.reviewCount();
            rate = Math.round((score.totalRate() / (double) score.reviewCount()) * 10) / 10.0;
        }

        return BookInfoResponse.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .cover(book.getCover())
                .categoryName(book.getCategoryName())
                .publisher(book.getPublisher())
                .rate(rate)
                .totalReview(count)
                .build();
    }


    public List<ReviewScore> getBookReviewScore(List<Long> bookIds) {

        if (bookIds == null || bookIds.isEmpty()) {
            return List.of();
        }

        List<ReviewScore> BookReview = bookRepository.findReviewScoreByBookId(bookIds);

        return BookReview;
    }

    public List<BookEntity> searchBook(String query) {

        if (variousFunc.isInvalidQuery(query)) {
            throw new ApiException(ErrorDefine.INVALID_ARGUMENT);
        }

        return bookRepository.searchByBookTitle(query);
    }

    @Transactional
    public BookEntity requestBook(addBookRequest dto) {
        BookEntity book = BookEntity.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .publisher(dto.getPublisher())
                .categoryName(dto.getCategory())
                .receivedFromAladin(false)
                .build();

        return bookRepository.save(book);
    }

    @Transactional
    public List<BookEntity> saveBook(List<AladinResponse> book) {

        List<BookEntity> data = book.stream()
                .map(BookEntity::toDatabase)
                .collect(Collectors.toList());

        List<String> ids = data.stream()
                .map(BookEntity::getItemId)
                .collect(Collectors.toList());

        List<BookEntity> existing = bookRepository.findAllByItemIdIn(ids);

        Set<String> existingIds = existing.stream()
                .map(BookEntity::getItemId)
                .collect(Collectors.toSet());

        List<BookEntity> toSave = data.stream()
                .filter(b -> !existingIds.contains(b.getItemId()))
                .collect(Collectors.toList());

        if (!toSave.isEmpty()) {
            bookRepository.saveAll(toSave);
        }

        List<BookEntity> response = bookRepository.findAllByItemIdIn(ids);

        return response;
    }
}
