package com.example.modam.domain.bookcase.Presentation;

import com.example.modam.domain.bookcase.Application.BookCaseService;
import com.example.modam.global.response.ResponseDTO;
import com.example.modam.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookcase")
public class BookCaseController {

    private BookCaseService bookCaseService;

    public BookCaseController(BookCaseService bookCaseService) {
        this.bookCaseService = bookCaseService;
    }

    @PostMapping
    public ResponseDTO create(@RequestBody BookCaseSaveRequestDTO dto,
                              @AuthenticationPrincipal CustomUserDetails user) {

        long userId = user.getUser().getId();
        long bookId = dto.getBookId();

        bookCaseService.saveUserBook(userId, bookId);

        return new ResponseDTO<>(
                "BookCase successfully created"
        );
    }

    @GetMapping
    public ResponseDTO<BookCaseResponse> read(@AuthenticationPrincipal CustomUserDetails user) {
        long userId = user.getUser().getId();

        BookCaseResponse data = bookCaseService.getUserBookCase(userId);

        return new ResponseDTO<>(
                data
        );
    }

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
