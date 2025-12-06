package com.example.modam.report;

import com.example.modam.domain.bookcase.Domain.BookCaseEntity;
import com.example.modam.domain.bookcase.Domain.BookState;
import com.example.modam.domain.bookcase.Interface.BookCaseRepository;
import com.example.modam.domain.report.Application.ReportService;
import com.example.modam.domain.report.Domain.Place;
import com.example.modam.domain.report.Domain.ReadingLogEntity;
import com.example.modam.domain.report.Interface.ReportRepository;
import com.example.modam.domain.report.Presentation.dto.ReadingLogResponse;
import com.example.modam.domain.report.Presentation.dto.RecordReadingLogRequest;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Interface.UserRepository;
import com.example.modam.global.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ReadingLogServiceTest {

    @Mock
    private BookCaseRepository bookCaseRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportService readingLogService;

    @Test
    @DisplayName("BookCase가 없으면 예외 발생")
    void bookcase_not_found_test() {
        RecordReadingLogRequest dto = mock(RecordReadingLogRequest.class);
        when(dto.getBookCaseId()).thenReturn(99L);

        when(bookCaseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> readingLogService.RecordReadingLog(dto, 1L));

        verify(bookCaseRepository).findById(99L);
        verifyNoInteractions(reportRepository);
    }

    @Test
    @DisplayName("유저 불일치 예외 발생")
    void user_not_match_test() {
        RecordReadingLogRequest dto = mock(RecordReadingLogRequest.class);
        when(dto.getBookCaseId()).thenReturn(1L);

        UserEntity owner = mock(UserEntity.class);
        UserEntity tokenUser = mock(UserEntity.class);

        BookCaseEntity bookCase = mock(BookCaseEntity.class);

        when(bookCaseRepository.findById(1L)).thenReturn(Optional.of(bookCase));
        when(bookCase.getUser()).thenReturn(owner);
        when(userRepository.getReferenceById(1L)).thenReturn(tokenUser);

        assertThrows(ApiException.class,
                () -> readingLogService.RecordReadingLog(dto, 1L));

        verify(bookCaseRepository).findById(1L);
        verifyNoInteractions(reportRepository);
    }

    @Test
    @DisplayName("BookCase 상태가 READING이 아니면 예외 발생")
    void invalid_status_test() {
        RecordReadingLogRequest dto = mock(RecordReadingLogRequest.class);
        when(dto.getBookCaseId()).thenReturn(2L);

        UserEntity owner = mock(UserEntity.class);
        BookCaseEntity bookCase = mock(BookCaseEntity.class);

        when(bookCaseRepository.findById(2L)).thenReturn(Optional.of(bookCase));
        when(bookCase.getUser()).thenReturn(owner);
        when(userRepository.getReferenceById(2L)).thenReturn(owner);
        when(bookCase.getStatus()).thenReturn(BookState.AFTER);

        assertThrows(ApiException.class,
                () -> readingLogService.RecordReadingLog(dto, 2L));

        verify(bookCaseRepository).findById(2L);
        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("ReadingLog 정상 저장")
    void save_log_test() {
        RecordReadingLogRequest dto = mock(RecordReadingLogRequest.class);
        when(dto.getBookCaseId()).thenReturn(3L);
        when(dto.getReadingPlace()).thenReturn(Place.HOME);

        UserEntity owner = mock(UserEntity.class);
        BookCaseEntity bookCase = mock(BookCaseEntity.class);

        when(bookCaseRepository.findById(3L)).thenReturn(Optional.of(bookCase));
        when(bookCase.getUser()).thenReturn(owner);
        when(userRepository.getReferenceById(3L)).thenReturn(owner);
        when(bookCase.getStatus()).thenReturn(BookState.READING);

        ArgumentCaptor<ReadingLogEntity> captor =
                ArgumentCaptor.forClass(ReadingLogEntity.class);

        when(reportRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReadingLogEntity saved = readingLogService.RecordReadingLog(dto, 3L);

        assertNotNull(saved);
        assertEquals(bookCase, saved.getBookCase());
        assertEquals(Place.HOME, saved.getReadingPlace());
        assertNotNull(saved.getReadAt());
        assertTrue(saved.getReadAt().isBefore(LocalDateTime.now().plusSeconds(1)));

        verify(bookCaseRepository).findById(3L);
        verify(reportRepository).save(any());
    }

    @DisplayName("유효한 year/month로 호출 시 start~end 범위를 정확히 계산하여 Repository를 호출")
    @Test
    void get_reading_log_success() {

        long userId = 10L;

        YearMonth ym = YearMonth.of(2025, 12);

        LocalDateTime expectedStart = ym.atDay(1).atStartOfDay();
        LocalDateTime expectedEnd = ym.atEndOfMonth().atTime(java.time.LocalTime.MAX);

        List<ReadingLogResponse> mockResponse = List.of(
                new ReadingLogResponse(
                        LocalDateTime.of(2025, 12, 5, 20, 30),
                        Place.HOME, "cover", "하하하"
                )
        );

        when(reportRepository.findByDate(any(), any(), eq(userId)))
                .thenReturn(mockResponse);

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        List<ReadingLogResponse> result = readingLogService.getReadingLog(2025, 12, userId);

        assertEquals(1, result.size());
        assertEquals(mockResponse, result);

        verify(reportRepository, times(1))
                .findByDate(startCaptor.capture(), endCaptor.capture(), eq(userId));

        assertEquals(expectedStart, startCaptor.getValue());
        assertEquals(expectedEnd, endCaptor.getValue());
    }

    @DisplayName("월이 1~12 범위가 아닐 때 예외 처리")
    @Test
    void get_reading_log_invalid_month() {
        int month = 0;
        int year = 2025;

        assertThrows(ApiException.class,
                () -> readingLogService.getReadingLog(year, month, 1L));

        verifyNoInteractions(reportRepository);
    }
}
