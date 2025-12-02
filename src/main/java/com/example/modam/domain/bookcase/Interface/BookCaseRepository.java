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

    List<BookCaseEntity> findByUser_Id(Long userId);

    Optional<BookCaseEntity> findByUser_IdAndBook_Id(Long userId, Long bookId);

    @Query(
            "select bc " +
                    "from bookcase bc " +
                    "join fetch bc.book b " +
                    "where bc.user.id = :userId and b.title like %:title% and bc.status = :state"
    )
    List<BookCaseEntity> searchByUserAndBookTitle(@Param("userId") Long userId,
                                                  @Param("title") String title, @Param("state") BookState state);
}
