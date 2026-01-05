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
        when(dto.getBookId()).thenReturn(99L);

        when(bookCaseRepository.findUserBookCaseId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> readingLogService.RecordReadingLog(dto, 1L));

        verify(bookCaseRepository).findUserBookCaseId(1L, 99L);
        verifyNoInteractions(reportRepository);
    }

    @Test
    @DisplayName("유저 불일치 예외 발생")
    void user_not_match_test() {
        RecordReadingLogRequest dto = mock(RecordReadingLogRequest.class);
        when(dto.getBookId()).thenReturn(1L);

        UserEntity owner = mock(UserEntity.class);
        UserEntity otherUser = mock(UserEntity.class);
        BookCaseEntity bookCase = mock(BookCaseEntity.class);

        when(bookCaseRepository.findUserBookCaseId(1L, 1L))
                .thenReturn(Optional.of(bookCase));
        when(bookCase.getUser()).thenReturn(owner);
        when(userRepository.getReferenceById(1L)).thenReturn(otherUser);

        assertThrows(ApiException.class,
                () -> readingLogService.RecordReadingLog(dto, 1L));

        verify(bookCaseRepository).findUserBookCaseId(1L, 1L);
        verifyNoInteractions(reportRepository);
    }

    @Test
    @DisplayName("BookCase 상태가 READING이 아니면 예외 발생")
    void invalid_status_test() {
        RecordReadingLogRequest dto = mock(RecordReadingLogRequest.class);
        when(dto.getBookId()).thenReturn(2L);

        UserEntity owner = mock(UserEntity.class);
        BookCaseEntity bookCase = mock(BookCaseEntity.class);

        when(bookCaseRepository.findUserBookCaseId(2L, 2L))
                .thenReturn(Optional.of(bookCase));
        when(bookCase.getUser()).thenReturn(owner);
        when(userRepository.getReferenceById(2L)).thenReturn(owner);
        when(bookCase.getStatus()).thenReturn(BookState.AFTER);

        assertThrows(ApiException.class,
                () -> readingLogService.RecordReadingLog(dto, 2L));

        verify(bookCaseRepository).findUserBookCaseId(2L, 2L);
        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("ReadingLog 정상 저장")
    void save_log_test() {
        RecordReadingLogRequest dto = mock(RecordReadingLogRequest.class);
        when(dto.getBookId()).thenReturn(3L);
        when(dto.getReadingPlace()).thenReturn(Place.HOME);

        UserEntity owner = mock(UserEntity.class);
        BookCaseEntity bookCase = mock(BookCaseEntity.class);

        when(bookCaseRepository.findUserBookCaseId(3L, 3L))
                .thenReturn(Optional.of(bookCase));
        when(bookCase.getUser()).thenReturn(owner);
        when(userRepository.getReferenceById(3L)).thenReturn(owner);
        when(bookCase.getStatus()).thenReturn(BookState.READING);

        ArgumentCaptor<ReadingLogEntity> captor = ArgumentCaptor.forClass(ReadingLogEntity.class);

        when(reportRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReadingLogEntity saved = readingLogService.RecordReadingLog(dto, 3L);

        assertNotNull(saved);
        assertEquals(bookCase, saved.getBookCase());
        assertEquals(Place.HOME, saved.getReadingPlace());
        assertNotNull(saved.getReadAt());

        verify(bookCaseRepository).findUserBookCaseId(3L, 3L);
        verify(reportRepository).save(any());
    }
}
