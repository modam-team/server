package com.example.modam.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class BookController {
    @Value("${aladin.ttb.key}")
    private String ttbKey;

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode searchBooks(String query, String queryType) throws Exception {
        if (query == null || query.isBlank() || queryType == null || queryType.isBlank()) {
            return new XmlMapper().createObjectNode();
        }

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder("http://www.aladin.co.kr/ttb/api/ItemSearch.aspx");
        sb.append("?ttbkey=").append(ttbKey);
        sb.append("&Query=").append(encodedQuery);
        sb.append("&QueryType=").append(queryType);
        sb.append("&MaxResults=20&start=1&output=xml&Version=20131101");

        URL url = new URL(sb.toString());
        try (InputStream in = url.openStream()) {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode root = xmlMapper.readTree(in);
            return root;
        }
    }
}
