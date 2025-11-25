package com.example.modam.domain.review.Interface;

import com.example.modam.domain.review.Domain.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    boolean existsByBookCase_Id(Long bookCaseId);
}
