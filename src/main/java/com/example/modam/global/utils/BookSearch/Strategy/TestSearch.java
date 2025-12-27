package com.example.modam.global.utils.BookSearch.Strategy;

import com.example.modam.domain.book.Presentation.dto.BookSearchRequest;
import com.example.modam.global.utils.BookSearch.BookSearchStrategy;
import com.example.modam.global.utils.BookSearch.QueryType;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class TestSearch implements BookSearchStrategy {

    @Override
    public QueryType support() {
        return QueryType.Test;
    }

    @Override
    public URL makeUrl(BookSearchRequest request) throws MalformedURLException {
        return new URL("http://localhost:9091/external/search");
    }
}
