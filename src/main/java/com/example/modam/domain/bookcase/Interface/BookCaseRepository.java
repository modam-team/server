package com.example.modam.domain.bookcase.Interface;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookCaseRepository extends JpaRepository<BookCaseEntity, Long> {
    boolean existsByUser_IdAndBook_Id(Long userId, Long bookId);

    List<BookCaseEntity> findByUser_Id(Long userId);

    Optional<BookCaseEntity> findByUser_IdAndBook_Id(Long userId, Long bookId);
}
