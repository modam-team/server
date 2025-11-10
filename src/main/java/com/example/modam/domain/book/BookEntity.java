package com.example.modam.domain.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "book")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class BookEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private String categoryName;

    private String cover;

    private String description;

    public static BookEntity toDatabase(BookInfoResponse dto) {
        return BookEntity.builder()
                .id(dto.getItemId())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .publisher(dto.getPublisher())
                .categoryName(dto.getCategoryName())
                .cover(dto.getCover())
                .description(dto.getDescription())
                .build();
    }
}
