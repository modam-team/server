package com.example.modam.domain.book.Facade;

import com.example.modam.domain.book.Application.BookDataService;
import com.example.modam.domain.book.Presentation.dto.BookInfoResponse;
import com.example.modam.domain.book.Application.BookService;
import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Presentation.dto.BookSearchRequest;
import com.example.modam.domain.book.Presentation.dto.ReviewScore;
import com.example.modam.domain.book.Presentation.dto.addBookRequest;
import com.example.modam.global.utils.BestSellerCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BookFacade {
    private final BookService bookService;
    private final BookDataService bookDataService;
    private final BestSellerCache bestSellerCache;

    public BookFacade(BookService bookService, BookDataService bookDataService, BestSellerCache bestSellerCache) {
        this.bookService = bookService;
        this.bookDataService = bookDataService;
        this.bestSellerCache = bestSellerCache;
    }

    // 알라딘 검색한 스레드가 이어서 DB에 저장하도록 연결하는 퍼사드, 베스트셀러는 서버 캐시에 저장
    public CompletableFuture<List<BookInfoResponse>> searchBook(BookSearchRequest dto) throws Exception {

        boolean isBestseller = "Bestseller".equals(dto.getQueryType());
        if (isBestseller) {
            CompletableFuture<List<BookInfoResponse>> cached = bestSellerCache.get();
            if (cached != null) {
                return cached;
            }
        }

        CompletableFuture<List<BookInfoResponse>> response =
                bookService.parseBookData(dto)
                        .thenApply(bookData -> {
                            List<BookEntity> entities = bookDataService.saveBook(bookData);

                            List<Long> bookIds = entities.stream()
                                    .map(BookEntity::getId)
                                    .distinct()
                                    .toList();

                            List<ReviewScore> scores = bookDataService.getBookReviewScore(bookIds);
                            Map<Long, ReviewScore> scoreMap = scores.stream()
                                    .collect(Collectors.toMap(ReviewScore::BookId, Function.identity()));

                            return entities.stream()
                                    .map(book -> bookDataService.toDto(book, scoreMap.get(book.getId())))
                                    .collect(Collectors.toList());
                        });

        if (isBestseller) {
            synchronized (bestSellerCache) {
                if (bestSellerCache.isExist()) {
                    CompletableFuture<List<BookInfoResponse>> already = bestSellerCache.get();
                    if (already != null) {
                        return already;
                    }
                }
                bestSellerCache.saveFuture(response);
            }
        }

        return response;
    }

    public void requestBook(long userId, addBookRequest dto) {
        log.info(
                "[Book Request] userId={}, title={}, author={}, publisher={}, category={}",
                userId,
                dto.getTitle(),
                dto.getAuthor(),
                dto.getPublisher(),
                dto.getCategory()
        );
        bookDataService.requestBook(dto);
    }
}
