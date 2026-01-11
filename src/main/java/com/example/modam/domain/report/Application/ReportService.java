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
import com.example.modam.global.utils.redis.RedisCharacterClient;
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
    private final RedisCharacterClient redisCharacterClient;

    private final long ONE_MONTH = 60L * 60 * 24 * 31;

    public List<ReadingLogResponse> getReadingLog(long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        List<ReadingLogResponse> response = reportRepository.findByDate(userId);

        return response;
    }

    public ReadingLogResponseWithTheme getReadingLog(long masterId, long otherId) {

        UserEntity user = userRepository.findById(masterId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        UserEntity other = userRepository.findById(otherId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        if (!other.isPublic() && friendRepository.findAcceptedFriendship(masterId, otherId).isEmpty()) {
            throw new ApiException(ErrorDefine.PRIVATE_RESOURCE_FORBIDDEN);
        }

        List<ReadingLogResponse> readingLog = reportRepository.getFinishReadingLog(otherId, BookState.AFTER);
        String theme = other.getThemeColor();
        ReadingLogResponseWithTheme response = new ReadingLogResponseWithTheme(readingLog, theme);

        return response;
    }

    public ReportResponse getReportData(long userId) {

        ReportBlock<Map<String, Map<String, List<ReportGroup>>>> data = calculateFinishLog(userId);
        ReportBlock<Map<String, Map<String, List<ReadReportGroup>>>> LogData = calculateReadingLog(userId);

        LocalDateTime current = LocalDateTime.now();
        int numMonth = current.getMonthValue() - 1;
        int numYear = current.getYear();
        if (numMonth == 0) {
            numMonth = 12;
            numYear = numYear - 1;
        }
        String year = String.valueOf(numYear);
        String month = String.format("%02d", numMonth);

        String cha_key = year + month + userId + "character";
        if (!redisCharacterClient.exists(cha_key)) {
            String[] forCharacter = variousFunc.decideCharacter(LogData, data);

            CharacterResponse characterResponse = CharacterResponse.builder()
                    .manyPlace(Place.valueOf(forCharacter[0]))
                    .readingTendency(forCharacter[1])
                    .build();

            redisCharacterClient.set(cha_key, characterResponse, ONE_MONTH);
        }

        CharacterResponse character = redisCharacterClient.get(cha_key);

        long userNum = 0;
        long characterNum = 0;

        if (!String.valueOf(character.getManyPlace()).equals("empty_data") && !character.getReadingTendency().equals("empty_data")) {
            userNum = userRepository.count();


            String key = year + month + character.getManyPlace() + "_" + character.getReadingTendency();

            if (!redisStringClient.exists(key)) {
                setReportRatio();
            }

            characterNum = Long.parseLong(redisStringClient.get(key));
        }

        ReportResponse response = ReportResponse.builder()
                .character(character)
                .data(data)
                .logData(LogData)
                .userTotalNum(userNum)
                .characterNum(characterNum)
                .build();

        return response;
    }

    public CharacterResponse getCharacter(long userId) {

        LocalDateTime current = LocalDateTime.now();
        int numMonth = current.getMonthValue() - 1;
        int numYear = current.getYear();
        if (numMonth == 0) {
            numMonth = 12;
            numYear = numYear - 1;
        }
        String year = String.valueOf(numYear);
        String month = String.format("%02d", numMonth);

        String key = year + month + userId + "character";
        if (redisCharacterClient.exists(key)) {
            return redisCharacterClient.get(key);
        }
        ReportBlock<Map<String, Map<String, List<ReportGroup>>>> data = calculateFinishLog(userId);
        ReportBlock<Map<String, Map<String, List<ReadReportGroup>>>> LogData = calculateReadingLog(userId);
        String[] forCharacter = variousFunc.decideCharacter(LogData, data);

        CharacterResponse characterResponse = CharacterResponse.builder()
                .manyPlace(Place.valueOf(forCharacter[0]))
                .readingTendency(forCharacter[1])
                .build();

        redisCharacterClient.set(key, characterResponse, ONE_MONTH);

        return characterResponse;
    }

    private ReportBlock<Map<String, Map<String, List<ReportGroup>>>> calculateFinishLog(long userId) {
        List<ReportRawData> rawList = reportRepository.findReportData(userId, BookState.AFTER);

        if (rawList.isEmpty()) {
            return new ReportBlock<>("EMPTY_FINISH", Map.of());
        }

        Map<GroupKey, List<String>> merged = rawList.stream()
                .collect(Collectors.groupingBy(
                        r -> new GroupKey(r.finishAt(), r.category()),
                        Collectors.mapping(
                                r -> r.rawHashtags() == null ? null : r.rawHashtags(),
                                Collectors.toList()
                        )
                ));

        List<ReportGroup> groupedList = merged.entrySet().stream()
                .map(e -> new ReportGroup(
                        e.getKey().finishAt(),
                        e.getKey().category(),
                        e.getValue().stream()
                                .filter(Objects::nonNull)
                                .toList()
                ))
                .toList();

        Map<String, Map<String, List<ReportGroup>>> data =
                groupedList.stream()
                        .collect(Collectors.groupingBy(
                                g -> String.valueOf(g.getFinishAt().getYear()),
                                Collectors.groupingBy(
                                        g -> String.valueOf(g.getFinishAt().getMonthValue())
                                )
                        ));

        return new ReportBlock<>("OK", data);
    }

    private ReportBlock<Map<String, Map<String, List<ReadReportGroup>>>> calculateReadingLog(long userId) {

        List<ReportLogRawData> rawList = reportRepository.findReadingLogData(userId);

        if (rawList.isEmpty()) {
            return new ReportBlock<>("EMPTY_LOG", Map.of());
        }

        Map<String, Map<String, List<ReadReportGroup>>> logData =
                rawList.stream()
                        .map(r -> new ReadReportGroup(
                                r.readAt(),
                                r.category(),
                                r.place()
                        ))
                        .collect(
                                Collectors.groupingBy(
                                        r -> String.valueOf(r.getReadAt().getYear()),
                                        Collectors.groupingBy(
                                                r -> String.valueOf(r.getReadAt().getMonthValue())
                                        )
                                )
                        );

        return new ReportBlock<>("OK", logData);
    }


    private void setReportRatio() {

        LocalDateTime current = LocalDateTime.now();
        int numMonth = current.getMonthValue() - 1;
        int numYear = current.getYear();
        if (numMonth == 0) {
            numMonth = 12;
            numYear = numYear - 1;
        }
        String year = String.valueOf(numYear);
        String month = String.format("%02d", numMonth);

        List<Long> userIds = userRepository.findAllUserIds();

        for (Long userId : userIds) {
            try {
                ReportBlock<Map<String, Map<String, List<ReadReportGroup>>>> logData = calculateReadingLog(userId);
                ReportBlock<Map<String, Map<String, List<ReportGroup>>>> data = calculateFinishLog(userId);

                String[] character = variousFunc.decideCharacter(logData, data);

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
