package com.example.modam.domain.book.Facade;

import com.example.modam.domain.book.Application.BookDataService;
import com.example.modam.domain.book.Presentation.dto.BookInfoResponse;
import com.example.modam.domain.book.Application.BookService;
import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Presentation.dto.BookSearchRequest;
import com.example.modam.domain.book.Presentation.dto.ReviewScore;
import com.example.modam.domain.book.Presentation.dto.addBookRequest;
import com.example.modam.global.config.Semaphore.AladinSemaphore;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.utils.BestSellerCache;
import com.example.modam.global.utils.VariousFunc;
import com.example.modam.global.utils.redis.RedisBookDataClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    private final VariousFunc variousFunc;
    private final RedisBookDataClient redisBookDataClient;
    private final long BOOK_SEARCH_TTL_SECONDS = 259_200L; // 3일

    @Qualifier("BookData")
    private final ThreadPoolTaskExecutor bookData;

    private final AladinSemaphore aladinSemaphore;

    public BookFacade(
            BookService bookService,
            BookDataService bookDataService,
            BestSellerCache bestSellerCache,
            VariousFunc variousFunc,
            RedisBookDataClient redisBookDataClient,
            @Qualifier("BookData") ThreadPoolTaskExecutor bookData,
            AladinSemaphore aladinSemaphore
    ) {
        this.bookService = bookService;
        this.bookDataService = bookDataService;
        this.bestSellerCache = bestSellerCache;
        this.variousFunc = variousFunc;
        this.redisBookDataClient = redisBookDataClient;
        this.bookData = bookData;
        this.aladinSemaphore=aladinSemaphore;
    }

    // 알라딘 검색한 스레드가 이어서 DB에 저장하도록 연결하는 퍼사드, 베스트셀러는 서버 캐시에 저장
    public CompletableFuture<List<BookInfoResponse>> searchBook(BookSearchRequest dto, long userId) throws Exception {

        log.info("[search book to Exterior API] userId={}, queryType={}, query={} ",
                userId, dto.getQueryType(), dto.getQuery());

        boolean isBestseller = "Bestseller".equals(dto.getQueryType());

        if (!isBestseller && variousFunc.isInvalidQuery(dto.getQuery())) {
            throw new ApiException(ErrorDefine.INVALID_ARGUMENT);
        }

        if (isBestseller) {
            CompletableFuture<List<BookInfoResponse>> cached = bestSellerCache.get();
            if (cached != null) {
                log.info("Bestseller is caching");
                return cached;
            }

        }

        if (!isBestseller && redisBookDataClient.exists(dto.getQuery())) {
            log.info("[Previous Query] Fast DB Search for query={}", dto.getQuery());

            return CompletableFuture.supplyAsync(() -> {
                List<BookEntity> entities = redisBookDataClient.get(dto.getQuery());

                List<Long> bookIds = entities.stream().map(BookEntity::getId).toList();
                Map<Long, ReviewScore> scoreMap =
                        bookDataService.getBookReviewScore(bookIds).stream()
                                .collect(Collectors.toMap(ReviewScore::BookId, Function.identity()));

                return entities.stream()
                        .map(book -> bookDataService.toDto(book, scoreMap.get(book.getId())))
                        .collect(Collectors.toList());
            });
        }

        aladinSemaphore.acquire();

        CompletableFuture<List<BookInfoResponse>> response;
        try {
            response = bookService.parseBookData(dto)
                    .thenApplyAsync(bookData -> {

                        List<BookEntity> entities;

                        if (isBestseller) {
                            entities = bookDataService.saveBook(bookData);
                        } else {
                            bookDataService.saveBook(bookData);
                            entities = bookDataService.searchBook(dto.getQuery());
                            redisBookDataClient.set(
                                    dto.getQuery(),
                                    entities,
                                    BOOK_SEARCH_TTL_SECONDS
                            );
                        }

                        List<Long> bookIds = entities.stream()
                                .map(BookEntity::getId)
                                .distinct()
                                .toList();

                        Map<Long, ReviewScore> scoreMap =
                                bookDataService.getBookReviewScore(bookIds).stream()
                                        .collect(Collectors.toMap(
                                                ReviewScore::BookId,
                                                Function.identity()
                                        ));

                        return entities.stream()
                                .map(book ->
                                        bookDataService.toDto(
                                                book,
                                                scoreMap.get(book.getId())
                                        )
                                )
                                .toList();

                    }, bookData)
                    .whenComplete((r, ex) -> aladinSemaphore.release());

        } catch (Exception e) {
            aladinSemaphore.release();
            throw e;
        }

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
