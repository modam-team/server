package com.example.modam.domain.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
}
