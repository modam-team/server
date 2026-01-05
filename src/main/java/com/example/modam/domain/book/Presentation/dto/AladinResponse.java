package com.example.modam.domain.book.Presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinResponse {
    private String itemId;
    private String title;
    private String author;
    private String cover;
    private String categoryName;
    private String publisher;
    private String link;

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
