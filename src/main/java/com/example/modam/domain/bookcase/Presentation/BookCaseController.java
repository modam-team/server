package com.example.modam.domain.bookcase.Presentation;

import com.example.modam.domain.bookcase.Application.BookCaseService;
import com.example.modam.domain.bookcase.Facade.BookCaseFacade;
import com.example.modam.global.response.ResponseDTO;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookcase")
public class BookCaseController {

    private BookCaseService bookCaseService;
    private BookCaseFacade bookCaseFacade;

    public BookCaseController(BookCaseService bookCaseService, BookCaseFacade bookCaseFacade) {
        this.bookCaseService = bookCaseService;
        this.bookCaseFacade = bookCaseFacade;
    }

    @Operation(
            summary = "책장에 책 넣기",
            description = "사용자가 책장에 넣을 책을 넣습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책장에 책 넣기 성공")
    })
    @PostMapping
    public ResponseDTO create(@RequestBody BookCaseSaveRequestDTO dto,
                              @AuthenticationPrincipal CustomUserDetails user) {

        long userId = user.getUser().getId();

        bookCaseService.saveUserBook(userId, dto);

        return new ResponseDTO<>(
                "BookCase successfully created"
        );
    }

    @Operation(
            summary = "책장에서 책 가져오기",
            description = "사용자가 책장에 넣을 책을 전/중/후 구분해서 가져옵니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책장에서 책 가져오기 성공")
    })
    @GetMapping
    public ResponseDTO<BookCaseResponse> read(@AuthenticationPrincipal CustomUserDetails user) {
        long userId = user.getUser().getId();

        BookCaseResponse data = bookCaseFacade.createBookCaseInfo(userId);

        return new ResponseDTO<>(
                data
        );
    }

    @Operation(
            summary = "책장에서 책 상태 변경",
            description = "사용자가 책장에 넣을 책의 상태를 전->중->후의 순서로 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책 상태 변경 성공")
    })
    @PatchMapping
    public ResponseDTO update(@RequestBody BookCaseSaveRequestDTO dto,
                              @AuthenticationPrincipal CustomUserDetails user) {

        long userId = user.getUser().getId();
        long bookId = dto.getBookId();

        bookCaseService.changeUserBook(userId, bookId);

        return new ResponseDTO<>(
                "BookCase successfully change status"
        );
    }
}
