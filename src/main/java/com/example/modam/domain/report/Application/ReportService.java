package com.example.modam.domain.report.Application;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.report.Domain.Place;
import com.example.modam.domain.report.Domain.ReadingLogEntity;
import com.example.modam.domain.report.Interface.ReportRepository;
import com.example.modam.domain.report.Presentation.dto.*;
import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.utils.VariousFunc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {
    private final ReportRepository reportRepository;
    private final BookCaseRepository bookCaseRepository;
    private final UserRepository userRepository;
    private final VariousFunc variousFunc;

    public ReportService(ReportRepository reportRepository, BookCaseRepository bookCaseRepository,
                         UserRepository userRepository, VariousFunc variousFunc) {
        this.reportRepository = reportRepository;
        this.bookCaseRepository = bookCaseRepository;
        this.userRepository = userRepository;
        this.variousFunc = variousFunc;
    }

    public List<ReadingLogResponse> getReadingLog(int year, int month, long userId) {

        if (month <= 0 || 13 <= month) {
            throw new ApiException(ErrorDefine.INVALID_ARGUMENT);
        }

        YearMonth ym = YearMonth.of(year, month);

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(LocalTime.MAX);

        List<ReadingLogResponse> response = reportRepository.findByDate(start, end, userId);

        return response;
    }

    public ReportResponse getReportData(long userId) {

        List<ReportRawData> rawList = reportRepository.findReportData(userId);

        if (rawList.isEmpty()) {
            throw new ApiException(ErrorDefine.REPORT_DATA_EMPTY);
        }

        Map<GroupKey, List<String>> merged = rawList.stream()
                .collect(Collectors.groupingBy(
                        r -> new GroupKey(r.readAt(), r.readingPlace(), r.category()),
                        Collectors.mapping(
                                r -> r.rawHashtags() == null ? null : r.rawHashtags(),
                                Collectors.toList()
                        )
                ));

        List<ReportGroup> groupedList = merged.entrySet().stream()
                .map(e -> new ReportGroup(
                        e.getKey().readAt(),
                        e.getKey().readingPlace(),
                        e.getKey().category(),
                        e.getValue().stream()
                                .filter(Objects::nonNull)
                                .toList()
                ))
                .toList();

        ReportResponse response = new ReportResponse();
        Map<String, Map<String, List<ReportGroup>>> data =
                groupedList.stream()
                        .collect(Collectors.groupingBy(
                                g -> String.valueOf(g.getReadAt().getYear()),
                                Collectors.groupingBy(
                                        g -> String.valueOf(g.getReadAt().getMonthValue())
                                )
                        ));
        response.setData(data);
        String[] forCharacter = variousFunc.decideCharacter(data);
        response.setCharacter(Place.valueOf(forCharacter[0]), forCharacter[1]);

        return response;
    }


    @Transactional
    public ReadingLogEntity RecordReadingLog(RecordReadingLogRequest dto, long userId) {
        Place place = dto.getReadingPlace();

        Optional<BookCaseEntity> bookCaseEntity = bookCaseRepository.findUserBookCaseId(userId, dto.getBookId());

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
