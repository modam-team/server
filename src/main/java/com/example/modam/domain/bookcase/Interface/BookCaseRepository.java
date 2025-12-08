package com.example.modam.domain.bookcase.Interface;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookCaseRepository extends JpaRepository<BookCaseEntity, Long> {
    boolean existsByUser_IdAndBook_Id(Long userId, Long bookId);

    Optional<BookCaseEntity> findByUser_IdAndBook_Id(Long userId, Long bookId);

    @Query(value = """
            SELECT bc.*
            FROM bookcase bc
            JOIN book b ON bc.book_id = b.id
            WHERE bc.user_id = :userId
              AND bc.status = :state
              AND MATCH(b.title) AGAINST(:query IN BOOLEAN MODE)
            """,
            nativeQuery = true)
    List<BookCaseEntity> searchByUserAndBookTitle(@Param("userId") Long userId,
                                                  @Param("query") String query, @Param("state") BookState state);

    @Query("""
            select bc
                from bookcase bc
                join fetch bc.book b
                where bc.user.id = :userId
            """)
    List<BookCaseEntity> findByUserIdWithBook(@Param("userId") Long userId);

    @Query("""
            select bc.id
            from bookcase bc
            where bc.user.id = :userId
            """)
    List<Long> findUserBookCaseIds(@Param("userId") long userId);
}
