package com.example.modam.domain.report.domain;


import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name="reading")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class ReadingLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookcase_id", nullable = false)
    private BookCaseEntity bookCase;

    private LocalDateTime readAt;

    @Enumerated(EnumType.STRING)
    private Place readingPlace;
}
