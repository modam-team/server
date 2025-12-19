package com.example.modam.domain.bookcase.Presentation.dto;

import com.example.modam.domain.bookcase.Domain.BookState;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookCaseSaveRequestDTO {
    private long bookId;
    private BookState state;
    private LocalDate startDate;
    private LocalDate endDate;
}
