package com.example.modam.domain.report.Interface;

import com.example.modam.domain.report.Domain.ReadingLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository  extends JpaRepository<ReadingLogEntity, Long> {
}
