package com.example.modam.domain.review.Interface;

import com.example.modam.domain.review.Domain.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    boolean existsByBookCase_Id(Long bookCaseId);

    Optional<ReviewEntity> findByBookCase_Id(long bookCaseId);
}
