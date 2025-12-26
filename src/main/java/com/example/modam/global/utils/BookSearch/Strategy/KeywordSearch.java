package com.example.modam.global.utils.BookSearch.Strategy;

import com.example.modam.domain.book.Presentation.dto.BookSearchRequest;
import com.example.modam.global.utils.BookSearch.BookSearchStrategy;
import com.example.modam.global.utils.BookSearch.QueryType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class KeywordSearch implements BookSearchStrategy {

    @Value("${aladin.ttb.key}")
    private String ttbKey;

    @Override
    public QueryType support() {
        return QueryType.Keyword;
    }

    @Override
    public URL makeUrl(BookSearchRequest dto) throws MalformedURLException {
        String query = dto.getQuery();
        String queryType = String.valueOf(dto.getQueryType());

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder("http://www.aladin.co.kr/ttb/api/ItemSearch.aspx");
        sb.append("?ttbkey=").append(ttbKey);
        sb.append("&Query=").append(encodedQuery);
        sb.append("&QueryType=").append(queryType);
        sb.append("&MaxResults=20&start=1&output=xml&Version=20131101");
        sb.append("&Cover=Big");

        URL url = new URL(sb.toString());
        return url;
    }
}
