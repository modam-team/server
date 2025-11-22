package com.example.modam.domain.bookcase;

import com.example.modam.domain.book.BookEntity;
import com.example.modam.domain.book.BookInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookCaseResponse {
    private List<BookInfoResponse> before;
    private List<BookInfoResponse> reading;
    private List<BookInfoResponse> after;
}
