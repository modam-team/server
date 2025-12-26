package com.example.modam.global.utils.BookSearch;

import com.example.modam.domain.book.Presentation.dto.BookSearchRequest;

import java.net.MalformedURLException;
import java.net.URL;

public interface BookSearchStrategy {
    QueryType support();

    URL makeUrl(BookSearchRequest request) throws MalformedURLException;
}
