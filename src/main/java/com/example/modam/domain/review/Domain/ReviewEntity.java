package com.example.modam.domain.review.Domain;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookcase_id", nullable = false)
    private BookCaseEntity bookCase;

    private int rating;

    private String hashtag;

    private String comment;
}
