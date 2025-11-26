package com.example.modam.domain.book.Presentation;

import com.example.modam.domain.book.Facade.BookFacade;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.response.ResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class BookController {

    private BookFacade bookFacade;

    public BookController(BookFacade bookFacade) {
        this.bookFacade = bookFacade;
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseDTO<List<BookInfoResponse>>> searchBooks(String query, String queryType) throws Exception {

        if (query == null || query.isBlank() || queryType == null || queryType.isBlank()) {
            throw new ApiException(ErrorDefine.INVALID_ARGUMENT);
        }

        return bookFacade.searchBook(query, queryType)
                .thenApply(bookList -> new ResponseDTO<>(bookList));
    }
}
