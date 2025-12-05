package com.example.modam.domain.book.Presentation;

import com.example.modam.domain.book.Facade.BookFacade;
import com.example.modam.domain.book.Presentation.dto.BookInfoResponse;
import com.example.modam.domain.book.Presentation.dto.BookSearchRequest;
import com.example.modam.domain.book.Presentation.dto.addBookRequest;
import com.example.modam.global.response.ResponseDTO;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@Tag(name = "Book", description = "독서 관련 API")
public class BookController {

    private BookFacade bookFacade;

    public BookController(BookFacade bookFacade) {
        this.bookFacade = bookFacade;
    }

    @Operation(
            summary = "책 검색",
            description = "사용자가 책장에 넣을 책을 검색합니다. " +
                    "queryType에서 Keyword라면 query를 기반으로 검색하고," +
                    "query가 없고 Bestseller만 있다면 알라딘의 주간 베스트셀러 5개를 검색합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책 검색 성공")
    })
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseDTO<List<BookInfoResponse>>> searchBooks(@RequestBody BookSearchRequest dto,
                                                                              @AuthenticationPrincipal CustomUserDetails user) throws Exception {
        long userId = user.getUser().getId();

        return bookFacade.searchBook(dto, userId)
                .thenApply(bookList -> new ResponseDTO<>(bookList));
    }

    @Operation(
            summary = "책 요청",
            description = "검색에서 나오지 않은 책을 관리자에게 요청합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책 요청 성공")
    })
    @PostMapping("/request")
    public ResponseDTO requestBook(@RequestBody addBookRequest dto,
                                   @AuthenticationPrincipal CustomUserDetails user) {

        long userId = user.getUser().getId();
        bookFacade.requestBook(userId, dto);

        return new ResponseDTO<>(
                "Request received successfully"
        );
    }
}
