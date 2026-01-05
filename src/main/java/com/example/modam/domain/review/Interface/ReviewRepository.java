package com.example.modam.domain.review.Interface;

import com.example.modam.domain.review.Domain.ReviewEntity;
import com.example.modam.domain.review.Presentation.dto.BookSearchReviewResponse;
import com.example.modam.domain.review.Presentation.dto.ReviewResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    boolean existsByBookCase_Id(Long bookCaseId);

    @Query("""
            select r
            from review r
            where r.bookCase.id in :BookCaseId
            """)
    List<ReviewEntity> findByBookCaseIds(List<Long> BookCaseId);

    @Query("""
            select r
            from review r
            where r.bookCase.book.id = :bookId
            and r.bookCase.user.id = :userId
            """)
    ReviewEntity findByBookIdAndUserId(@Param("bookId") long bookId, @Param("userId") long userId);

    @Query("""
            select u.nickname,r.rating, r.comment, u.profileImageUrl
            from review r
            join r.bookCase bc
            join bc.book b
            join bc.user u
            where b.id=:bookId
            """)
    List<BookSearchReviewResponse> findBookSearchData(@Param("bookId") long bookId);
}
