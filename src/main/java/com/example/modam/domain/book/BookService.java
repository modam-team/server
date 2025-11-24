package com.example.modam.domain.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BookService {
    @Value("${aladin.ttb.key}")
    private String ttbKey;

    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;
    private final CategoryMapping categoryMapping;

    public BookService(XmlMapper xmlMapper, ObjectMapper jsonMapper, CategoryMapping categoryMapping) {
        this.jsonMapper = jsonMapper;
        this.xmlMapper = xmlMapper;
        this.categoryMapping = categoryMapping;
    }

    // API를 불러올 URL을 형성
    public URL makeUrl(String query, String queryType) throws Exception {
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

    // 응답 데이터인 XML을 응답 JSON으로 가공
    @Async("aladin")
    public CompletableFuture<List<AladinResponse>> parseBookData(String query, String queryType) throws Exception {
        URL url = makeUrl(query, queryType);

        try (InputStream in = url.openStream()) {
            JsonNode root = xmlMapper.readTree(in);
            JsonNode itemsNode = root.path("item");
            List<AladinResponse> result = new ArrayList<>();

            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    AladinResponse book = jsonMapper.convertValue(item, AladinResponse.class);
                    String newCategory = preprocessCategory(book.getCategoryName());
                    if (!newCategory.equals("impossible category")) {
                        book.setCategoryName(newCategory);
                        result.add(book);
                    }
                }
            } else if (itemsNode.isObject()) { // 데이터가 1개만 오는 경우
                AladinResponse book = jsonMapper.convertValue(itemsNode, AladinResponse.class);
                String newCategory = preprocessCategory(book.getCategoryName());
                if (!newCategory.equals("impossible category")) {
                    book.setCategoryName(newCategory);
                    result.add(book);
                }
            }

            return CompletableFuture.completedFuture(result);
        }
    }

    // 입력데이터는 가공되지 않은 카테고리 데이터
    // ex) 국내도서>소설/시/희곡>추리/미스터리소설>영미 추리/미스터리소설
    public String preprocessCategory(String s) {
        String[] parseData = s.split(">");
        String parsedCategory = parseData[1].trim();
        String firstDepth = parseData[0].trim();

        if (categoryMapping.isImpossible(parsedCategory)) {
            return "impossible category";
        }

        String mapped = categoryMapping.mapCategory(parsedCategory);
        if (mapped != null && !mapped.isBlank()) {
            return mapped;
        }

        if (firstDepth.equals("해외도서")) {
            return firstDepth + ">" + parsedCategory;
        }

        return parsedCategory; // 테스트용으로 일단은 예외 카테고리도 반환. 나중에 바꿀 예정
    }
}
