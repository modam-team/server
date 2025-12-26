package com.example.modam.global.utils.BookSearch.Strategy;

import com.example.modam.domain.book.Presentation.dto.BookSearchRequest;
import com.example.modam.global.utils.BookSearch.BookSearchStrategy;
import com.example.modam.global.utils.BookSearch.QueryType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class BestsellerSearch implements BookSearchStrategy {

    @Value("${aladin.ttb.key}")
    private String ttbKey;

    @Override
    public QueryType support() {
        return QueryType.Bestseller;
    }

    @Override
    public URL makeUrl(BookSearchRequest dto) throws MalformedURLException {
        String queryType = String.valueOf(dto.getQueryType());

        StringBuilder sb = new StringBuilder("http://www.aladin.co.kr/ttb/api/ItemList.aspx");
        sb.append("?ttbkey=").append(ttbKey);
        sb.append("&QueryType=").append(queryType);
        sb.append("&SearchTarget=Book");
        sb.append("&MaxResults=6");
        sb.append("&start=1");
        sb.append("&output=xml");
        sb.append("&Version=20131101");
        sb.append("&Cover=Big");
        return new URL(sb.toString());
    }

}
