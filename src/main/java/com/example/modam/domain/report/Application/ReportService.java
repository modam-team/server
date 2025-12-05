package com.example.modam.domain.report.Application;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.report.Domain.Place;
import com.example.modam.domain.report.Domain.ReadingLogEntity;
import com.example.modam.domain.report.Interface.ReportRepository;
import com.example.modam.domain.report.Presentation.dto.ReadingLogRequest;
import com.example.modam.domain.report.Presentation.dto.ReadingLogResponse;
import com.example.modam.domain.report.Presentation.dto.RecordReadingLogRequest;
import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ReportService {
    private final ReportRepository reportRepository;
    private final BookCaseRepository bookCaseRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, BookCaseRepository bookCaseRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.bookCaseRepository = bookCaseRepository;
        this.userRepository = userRepository;
    }

    public List<ReadingLogResponse> getReadingLog(ReadingLogRequest dto, long userId) {

        if (dto.getMonth() <= 0 || 13 <= dto.getMonth()) {
            throw new ApiException(ErrorDefine.INVALID_ARGUMENT);
        }

        YearMonth ym = YearMonth.of(dto.getYear(), dto.getMonth());

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(LocalTime.MAX);

        List<ReadingLogResponse> response = reportRepository.findByDate(start, end, userId);

        return response;
    }

    @Transactional
    public ReadingLogEntity RecordReadingLog(RecordReadingLogRequest dto, long userId) {
        long bookCaseId = dto.getBookCaseId();
        Place place = dto.getReadingPlace();

        Optional<BookCaseEntity> bookCaseEntity = bookCaseRepository.findById(bookCaseId);

        if (bookCaseEntity.isEmpty() || !bookCaseEntity.get().getUser().equals(userRepository.getReferenceById(userId))) {
            throw new ApiException(ErrorDefine.INVALID_ARGUMENT);
        }

        if (bookCaseEntity.get().getStatus() != BookState.READING) {
            throw new ApiException(ErrorDefine.UNAUTHORIZED_STATUS);
        }

        ReadingLogEntity readingLogEntity = ReadingLogEntity.builder()
                .bookCase(bookCaseEntity.get())
                .readAt(LocalDateTime.now())
                .readingPlace(place)
                .build();

        return reportRepository.save(readingLogEntity);
    }
}
