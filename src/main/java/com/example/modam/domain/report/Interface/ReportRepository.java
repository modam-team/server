package com.example.modam.domain.report.Interface;

import com.example.modam.domain.report.Domain.ReadingLogEntity;
import com.example.modam.domain.report.Presentation.dto.ReadingLogResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<ReadingLogEntity, Long> {

    @Query("""
            select new com.example.modam.domain.report.Presentation.dto.ReadingLogResponse(
            r.readAt,r.readingPlace
            )
            from reading r
            join r.bookCase bc
            join bc.user
            where user.id=:userId
            and r.readAt between :start and :end
            """)
    List<ReadingLogResponse> findByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                        @Param("userId") long userId);
}
