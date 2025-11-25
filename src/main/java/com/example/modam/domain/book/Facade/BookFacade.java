package com.example.modam.domain.book.Facade;

import com.example.modam.domain.book.Application.BookDataService;
import com.example.modam.domain.book.Presentation.BookInfoResponse;
import com.example.modam.domain.book.Application.BookService;
import com.example.modam.domain.book.Domain.BookEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class BookFacade {
    private final BookService bookService;
    private final BookDataService bookDataService;

    public BookFacade(BookService bookService, BookDataService bookDataService) {
        this.bookService = bookService;
        this.bookDataService = bookDataService;
    }

    // 알라딘 검색한 스레드가 이어서 DB에 저장하도록 연결하는 퍼사드
    public CompletableFuture<List<BookInfoResponse>> searchBook(String query, String queryType) throws Exception {
        return bookService.parseBookData(query, queryType).thenApply(bookData -> {
            List<BookEntity> entities = bookDataService.saveBook(bookData);
            return entities.stream()
                    .map(bookDataService::toDto)
                    .collect(Collectors.toList());
        });
    }
}
