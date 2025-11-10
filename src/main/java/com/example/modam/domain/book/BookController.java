package com.example.modam.domain.book;

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
        this.bookFacade=bookFacade;
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseDTO<List<BookEntity>>> searchBooks(String query, String queryType) throws Exception {

        if (query == null || query.isBlank() || queryType == null || queryType.isBlank()) {
            throw new ApiException(ErrorDefine.INVALID_ARGUMENT);
        }

        return bookFacade.searchBook(query, queryType)
                .thenApply(bookList -> new ResponseDTO<>(bookList));
    }
    // 컨트롤러 레이어에서는 입력 데이터를 점검하고 서비스 레이어에 데이터를 보내준다.
    // 서비스 레이어에서는 URL을 만들고, 외부에서 데이터를 받아와서 responseDTO에 대응하는 데이터를 가져와준다.
    // 컨트롤러 레이어에서는 서비스 레이어에서 받아온 데이터를 API 응답으로 보내준다.
}
