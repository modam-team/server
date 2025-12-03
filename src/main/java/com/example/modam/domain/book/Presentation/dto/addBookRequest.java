package com.example.modam.domain.book.Presentation.dto;

import lombok.Getter;

@Getter
public class addBookRequest {
    private String title;
    private String author;
    private String publisher;
    private String category;
}
