package com.example.modam.domain.bookcase.Presentation;

import com.example.modam.domain.bookcase.Domain.BookState;
import lombok.Getter;

@Getter
public class BookCaseSaveRequestDTO {
    private long bookId;
    private BookState state;
}
