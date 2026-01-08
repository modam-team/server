package com.example.modam.domain.book.Application;

import com.example.modam.domain.book.Presentation.dto.BookSearchRequest;
import com.example.modam.global.utils.BookSearch.BookSearchFactory;
import com.example.modam.global.utils.CategoryMapping;
import com.example.modam.domain.book.Presentation.dto.AladinResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BookService {

    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;
    private final CategoryMapping categoryMapping;
    private final BookSearchFactory bookSearchFactory;

    // 응답 데이터인 XML을 응답 JSON으로 가공
    @Async("aladin")
    public CompletableFuture<List<AladinResponse>> parseBookData(BookSearchRequest dto) throws Exception {

        URL url = bookSearchFactory.requestedUrl(dto);

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
                        book.setCover(preprocessCover(book.getCover()));
                        result.add(book);
                    }
                    if (dto.getQueryType().toString().equals("Bestseller") && result.size() == 5) {
                        break;
                    }
                }
            } else if (itemsNode.isObject()) { // 데이터가 1개만 오는 경우
                AladinResponse book = jsonMapper.convertValue(itemsNode, AladinResponse.class);
                String newCategory = preprocessCategory(book.getCategoryName());
                if (!newCategory.equals("impossible category")) {
                    book.setCover(preprocessCover(book.getCover()));
                    book.setCategoryName(newCategory);
                    result.add(book);
                }
            }

            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            System.err.println("Fail to Call Aladin API: " + e.getMessage());
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
    }

    public String preprocessCover(String cover) {

        String[] parts = cover.split("/");

        String coverPart = parts[parts.length - 2];

        if (coverPart.startsWith("cover")) {
            parts[parts.length - 2] = "cover500";
        } else {
            return cover;
        }

        return String.join("/", parts);
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
