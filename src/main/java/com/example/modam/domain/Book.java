package com.example.modam.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Table(name="Book")
public class Book{
    // 도서 id (pk)
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id",columnDefinition="long")
    private Long id;

    // 책 제목
    @Column(name="title", nullable=false, length=255)
    private String title;

    // 작가
    @Column(name="author", length=255)
    private String author;

    // 장르
    @Column(name="genre", length=50)
    private String genre;

    // 책 이미지 URL
    @Column(name="image", columnDefinition="text")
    private String image;

    // 형식
    @Column(name="format", length=50)
    private String format;

    @Builder
    public Book(String title, String author, String genre, String image, String format){
        this.title=title;
        this.author=author;
        this.genre=genre;
        this.image=image;
        this.format=format;
    }
}