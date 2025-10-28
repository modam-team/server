package com.example.modam.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class BookService {
    @Value("${aladin.ttb.key}")
    private String ttbKey;

    public URL makeUrl(String query, String queryType) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder("http://www.aladin.co.kr/ttb/api/ItemSearch.aspx");
        sb.append("?ttbkey=").append(ttbKey);
        sb.append("&Query=").append(encodedQuery);
        sb.append("&QueryType=").append(queryType);
        sb.append("&MaxResults=20&start=1&output=xml&Version=20131101");

        URL url = new URL(sb.toString());
        return url;
    }

    public JsonNode parseBookData (String query, String queryType) throws Exception {
        URL url = makeUrl(query, queryType);

        try (InputStream in = url.openStream()) {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode root = xmlMapper.readTree(in);
            return root;
        }
    }
}
