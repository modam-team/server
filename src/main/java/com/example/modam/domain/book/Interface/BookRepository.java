package com.example.modam.domain.book.Interface;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Presentation.dto.ReviewScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    List<BookEntity> findAllByItemIdIn(List<String> itemIds);

    @Query("""
                SELECT new com.example.modam.domain.book.Presentation.ReviewScore(
                    COUNT(r),
                    COALESCE(SUM(r.rating), 0)
                )
                FROM review r
                JOIN r.bookCase bc
                JOIN bc.book b
                WHERE b.id = :bookId
            """)
    ReviewScore findReviewScoreByBookId(@Param("bookId") Long bookId);
}
