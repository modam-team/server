package com.example.modam.domain.bookcase;

import com.example.modam.global.response.ResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookcase")
public class BookCaseController {

    private BookCaseService bookCaseService;

    public BookCaseController(BookCaseService bookCaseService) {
        this.bookCaseService = bookCaseService;
    }

    @PostMapping
    public ResponseDTO create(@RequestBody BookCaseSaveRequestDTO dto) {
        long userId = dto.getUserId();
        long bookId = dto.getBookId();

        bookCaseService.saveUserBook(userId, bookId);

        return new ResponseDTO<>(
                "BookCase successfully created"
        );
    }
}
