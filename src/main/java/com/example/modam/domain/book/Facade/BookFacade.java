package com.example.modam.domain.book.Facade;

import com.example.modam.domain.book.Application.BookDataService;
import com.example.modam.domain.book.Presentation.BookInfoResponse;
import com.example.modam.domain.book.Application.BookService;
import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Presentation.BookSearchRequest;
import com.example.modam.global.utils.BestSellerCache;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
                            return entities.stream()
                                    .map(bookDataService::toDto)
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
}
