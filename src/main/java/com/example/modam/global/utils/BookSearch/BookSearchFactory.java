package com.example.modam.global.utils.BookSearch;

import com.example.modam.domain.book.Presentation.dto.BookSearchRequest;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BookSearchFactory {
    private Map<QueryType, BookSearchStrategy> strategyMap = new HashMap<>();

    public BookSearchFactory(
            List<BookSearchStrategy> strategies
    ) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        BookSearchStrategy::support,
                        Function.identity()
                ));
    }

    public URL requestedUrl(BookSearchRequest request) throws MalformedURLException {
        QueryType queryType = request.getQueryType();
        BookSearchStrategy strategy = strategyMap.get(queryType);

        return strategy.makeUrl(request);
    }
}
