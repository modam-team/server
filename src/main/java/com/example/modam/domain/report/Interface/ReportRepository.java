package com.example.modam.domain.report.Interface;

import com.example.modam.domain.report.Domain.ReadingLogEntity;
import com.example.modam.domain.report.Presentation.dto.ReadingLogResponse;
import com.example.modam.domain.report.Presentation.dto.ReportResponse;
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
            and r.readAt between :start and :end
            """)
    List<ReadingLogResponse> findByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                        @Param("userId") long userId);

    @Query(value = """
            select 
                r.read_at,
                r.reading_place,
                b.category_name,
                group_concat(ht.tag separator ' ')
            from reading r
            join book_case bc on r.book_case_id = bc.id
            join user u on bc.user_id = u.id
            join book b on bc.book_id = b.id
            left join review rev on rev.book_case_id = bc.id
            left join hashtag ht on ht.review_id = rev.id
            where u.id = :userId
            group by r.read_at, r.reading_place, b.category_name
            """, nativeQuery = true)
    List<ReportResponse> findReportData(long userId);

}
