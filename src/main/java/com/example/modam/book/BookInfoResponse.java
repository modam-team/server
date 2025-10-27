package com.example.modam.book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookInfoResponse {
    private String title;
    private String author;
}
