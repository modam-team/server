package com.example.modam.domain.book.Application;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Interface.BookRepository;
import com.example.modam.domain.book.Presentation.dto.*;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.utils.VariousFunc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookDataService {

    private final BookRepository bookRepository;
    private final VariousFunc variousFunc;

    public BookDataService(BookRepository bookRepository, VariousFunc variousFunc) {
        this.bookRepository = bookRepository;
        this.variousFunc = variousFunc;
    }

    public BookInfoResponse toDto(BookEntity book, BookReviewResponse review) {
        long count = 0;
        double rate = 0;
        if (review != null && review.reviewScore() != null && review.reviewScore().reviewCount() > 0) {
            count = review.reviewScore().reviewCount();
            rate = Math.round((review.reviewScore().totalRate() / (double) review.reviewScore().reviewCount()) * 10) / 10.0;
        }

        List<String> hashtags = new ArrayList<>();
        if (review != null && review.hashtags() != null) {
            hashtags = review.hashtags();
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
                .link(book.getLink())
                .hashtags(hashtags)
                .build();
    }


    public List<BookReviewResponse> getBookReviewScore(List<Long> bookIds) {

        if (bookIds == null || bookIds.isEmpty()) {
            return List.of();
        }

        List<ReviewScore> scores = bookRepository.findReviewScoreByBookId(bookIds);
        List<BookHashtagRecord> hashtagRows = bookRepository.findHashtagByBookIds(bookIds);
        Map<Long, List<String>> hashtagMap =
                hashtagRows.stream()
                        .collect(Collectors.groupingBy(
                                row -> row.bookId(),
                                Collectors.mapping(
                                        row -> row.hashtag(),
                                        Collectors.toList()
                                )
                        ));

        Map<Long, List<String>> topHashtag = new HashMap<>();
        for (Long l : hashtagMap.keySet()) {
            List<String> hashtagSet = hashtagMap.get(l);
            List<String> topSet = getThreeHashtag(hashtagSet);
            topHashtag.put(l, topSet);
        }

        return scores.stream()
                .map(score -> new BookReviewResponse(
                        score.BookId(),
                        score,
                        topHashtag.getOrDefault(score.BookId(), List.of())
                ))
                .toList();
    }


    public List<BookEntity> searchBook(String query) {

        if (variousFunc.isInvalidQuery(query)) {
            throw new ApiException(ErrorDefine.INVALID_ARGUMENT);
        }

        return bookRepository.searchByBookTitle(query);
    }

    private List<String> getThreeHashtag(List<String> hashtags) {

        HashMap<String, Long> map = new HashMap<>();

        for (String s : hashtags) {
            map.merge(s, 1L, Long::sum);
        }

        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
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
