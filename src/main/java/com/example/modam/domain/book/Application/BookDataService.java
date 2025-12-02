package com.example.modam.domain.book.Application;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Interface.BookRepository;
import com.example.modam.domain.book.Presentation.dto.AladinResponse;
import com.example.modam.domain.book.Presentation.dto.BookInfoResponse;
import com.example.modam.domain.book.Presentation.dto.ReviewScore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookDataService {

    private final BookRepository bookRepository;

    public BookDataService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookInfoResponse toDto(BookEntity book) {
        ReviewScore score = bookRepository.findReviewScoreByBookId(book.getId());

        long count = 0;
        double rate = 0;
        if (score != null) {
            count = score.reviewCount();
            rate = Math.round((score.totalRate() / (double) score.reviewCount()) * 10) / 10.0;
        }

        BookInfoResponse info = BookInfoResponse.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .cover(book.getCover())
                .categoryName(book.getCategoryName())
                .publisher(book.getPublisher())
                .rate(rate)
                .totalReview(count)
                .build();

        return info;

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
