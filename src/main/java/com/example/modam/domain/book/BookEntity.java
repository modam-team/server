package com.example.modam.domain.book;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "book")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private String categoryName;

    private String cover;

    private String itemId; // when Aladin save book, no repetition

    public static BookEntity toDatabase(BookInfoResponse dto) {
        return BookEntity.builder()
                .itemId(dto.getItemId())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .publisher(dto.getPublisher())
                .categoryName(dto.getCategoryName())
                .cover(dto.getCover())
                .build();
    }

    public BookInfoResponse toDto() {
        return BookInfoResponse.builder()
                .title(title)
                .author(author)
                .publisher(publisher)
                .categoryName(categoryName)
                .cover(cover)
                .itemId(itemId)
                .build();
    }
}
