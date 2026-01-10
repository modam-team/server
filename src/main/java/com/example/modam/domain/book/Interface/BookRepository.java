package com.example.modam.domain.book.Interface;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Presentation.dto.BookHashtagRecord;
import com.example.modam.domain.book.Presentation.dto.ReviewScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    List<BookEntity> findAllByItemIdIn(List<String> itemIds);

    @Query("""
            select new com.example.modam.domain.book.Presentation.dto.ReviewScore(
                    b.id, count(r), coalesce(sum(r.rating), 0)
                )
                from review r
                join r.bookCase bc
                join bc.book b
                where b.id in :bookIds
                group by b.id
            """)
    List<ReviewScore> findReviewScoreByBookId(@Param("bookIds") List<Long> bookIds);

    @Query(value = """
            select *
            from book b
            where b.received_from_aladin = true
            and match(b.title) against(:query in boolean mode)
            """, nativeQuery = true)
    List<BookEntity> searchByBookTitle(@Param("query") String query);

    @Query("""
            select b
            from book b
            where b.categoryName in :category
            and b.id not in :userBook
            and b.receivedFromAladin=true
            """)
    List<BookEntity> recommendByBookCategory(@Param("category") List<String> category,
                                             @Param("userBook") List<Long> userBook);

    @Query("""
            select new com.example.modam.domain.book.Presentation.dto.BookHashtagRecord(
                        b.id, h.tag
                        )
            from hashtag h
            join h.review r
            join r.bookCase bc
            join bc.book b
            where b.id in :bookIds
            """)
    List<BookHashtagRecord> findHashtagByBookIds(@Param("bookIds") List<Long>bookIds);
}
