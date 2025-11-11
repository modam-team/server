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

    private String itemId; // 알라딘에서 책을 저장할때 레코드가 중복되지 않기 위함

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
}
