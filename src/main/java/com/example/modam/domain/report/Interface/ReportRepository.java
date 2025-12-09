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

    @Query("""
            select new com.example.modam.domain.report.Presentation.dto.ReportResponse(
            r.readAt,r.readingPlace,b.categoryName,string_agg(ht.tag, ' ')
            )
            from reading r
            join r.bookCase bc
            join bc.user u
            join bc.book b
            left join review rev on rev.bookCase=bc
            left join rev.hashtags ht
            where u.id=:userId
            group by r.readAt, r.readingPlace, b.categoryName
            """)
    List<ReportResponse> findReportData(@Param("userId") long userId);
}
