package com.example.modam.domain.review.Domain;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookcase_id", nullable = false)
    private BookCaseEntity bookCase;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HashtagEntity> hashtags = new ArrayList<>();

    @Builder
    public ReviewEntity(BookCaseEntity bookCase, int rating, String comment) {
        this.bookCase = bookCase;
        this.rating = rating;
        this.comment = comment;
        this.hashtags = new ArrayList<>();
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
