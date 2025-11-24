package com.example.modam.domain.bookcase.Domain;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "bookcase")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class BookCaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Enumerated(EnumType.STRING)
    private BookState status;

    private LocalDateTime enrollAt;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    public void setStatus(BookState state) {
        this.status = state;
    }

    public void setStartedAt(LocalDateTime time) {
        this.startedAt = time;
    }

    public void setFinishedAt(LocalDateTime time) {
        this.finishedAt = time;
    }

}
