package com.example.modam.domain.report.Interface;

import com.example.modam.domain.report.Domain.ReadingLogEntity;
import com.example.modam.domain.report.Presentation.dto.ReadingLogResponse;
import com.example.modam.domain.report.Presentation.dto.ReportRawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<ReadingLogEntity, Long> {

    @Query("""
            select new com.example.modam.domain.report.Presentation.dto.ReadingLogResponse(
            r.readAt,r.readingPlace, b.cover, b.title
            )
            from reading r
            join r.bookCase bc
            join bc.user
            join bc.book b
            where user.id=:userId
            """)
    List<ReadingLogResponse> findByDate(@Param("userId") long userId);

    @Query("""
    select new com.example.modam.domain.report.Presentation.dto.ReportRawData(
        r.readAt,
        r.readingPlace,
        b.categoryName,
        ht.tag
    )
    from reading r
    join bookcase bc on r.bookCase.id = bc.id
    join user u on bc.user.id = u.id
    join book b on bc.book.id = b.id
    left join review rev on rev.bookCase.id = bc.id
    left join hashtag ht on ht.review.id = rev.id
    where u.id = :userId
""")
    List<ReportRawData> findReportData(@Param("userId") long userId);

}
