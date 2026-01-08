package com.example.modam.domain.report.Application;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.friend.Interface.FriendRepository;
import com.example.modam.domain.report.Domain.Place;
import com.example.modam.domain.report.Domain.ReadingLogEntity;
import com.example.modam.domain.report.Interface.ReportRepository;
import com.example.modam.domain.report.Presentation.dto.*;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.utils.VariousFunc;
import com.example.modam.global.utils.redis.RedisStringClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final BookCaseRepository bookCaseRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final VariousFunc variousFunc;
    private final RedisStringClient redisStringClient;

    public List<ReadingLogResponse> getReadingLog(long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        List<ReadingLogResponse> response = reportRepository.findByDate(userId);

        return response;
    }

    public List<ReadingLogResponse> getReadingLog(long masterId, long otherId) {

        UserEntity user = userRepository.findById(masterId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        UserEntity other = userRepository.findById(otherId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        if (!other.isPublic() && friendRepository.findAcceptedFriendship(masterId, otherId).isEmpty()) {
            throw new ApiException(ErrorDefine.PRIVATE_RESOURCE_FORBIDDEN);
        }

        List<ReadingLogResponse> response = reportRepository.findByDate(otherId);

        return response;
    }

    public ReportResponse getReportData(long userId) {


        Map<String, Map<String, List<ReportGroup>>> data = calculateReadingLog(userId);

        String[] forCharacter = variousFunc.decideCharacter(data);
        long userNum = 0;
        long characterNum = 0;

        if (!forCharacter[0].equals("empty_data") && !forCharacter[1].equals("empty_data")) {
            userNum = userRepository.count();

            LocalDateTime current = LocalDateTime.now();
            String year = String.valueOf(current.getYear());
            String month = String.valueOf(current.getMonthValue());

            String key = year + month + forCharacter[0] + "_" + forCharacter[1];

            if (!redisStringClient.exists(key)) {
                setReportRatio();
            }

            characterNum = Long.parseLong(redisStringClient.get(key));
        }

        ReportResponse response = ReportResponse.builder()
                .manyPlace(Place.valueOf(forCharacter[0]))
                .readingTendency(forCharacter[1])
                .data(data)
                .userTotalNum(userNum)
                .characterNum(characterNum)
                .build();

        return response;
    }

    private Map<String, Map<String, List<ReportGroup>>> calculateReadingLog(long userId) {
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

        Map<String, Map<String, List<ReportGroup>>> data =
                groupedList.stream()
                        .collect(Collectors.groupingBy(
                                g -> String.valueOf(g.getReadAt().getYear()),
                                Collectors.groupingBy(
                                        g -> String.valueOf(g.getReadAt().getMonthValue())
                                )
                        ));

        return data;
    }

    private void setReportRatio() {

        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String month = String.valueOf(now.getMonthValue());

        List<Long> userIds = userRepository.findAllUserIds();

        for (Long userId : userIds) {
            try {
                Map<String, Map<String, List<ReportGroup>>> data = calculateReadingLog(userId);

                String[] character = variousFunc.decideCharacter(data);

                if ("empty_data".equals(character[0]) || "empty_data".equals(character[1])) {
                    continue;
                }

                String key = year + month + character[0] + "_" + character[1];
                redisStringClient.increment(key);

            } catch (ApiException e) {
                // 읽기 데이터가 없을 때
                continue;
            }
        }
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
