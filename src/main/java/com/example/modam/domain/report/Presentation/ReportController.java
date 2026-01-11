package com.example.modam.domain.report.Presentation;

import com.example.modam.domain.report.Application.ReportService;
import com.example.modam.domain.report.Presentation.dto.ReadingLogResponse;
import com.example.modam.domain.report.Presentation.dto.ReadingLogResponseWithTheme;
import com.example.modam.domain.report.Presentation.dto.RecordReadingLogRequest;
import com.example.modam.domain.report.Presentation.dto.ReportResponse;
import com.example.modam.global.response.ResponseDTO;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@Tag(name = "Report", description = "독서 기록/리포트 관련 API")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(
            summary = "독서 기록 저장하기",
            description = "책장에 있는 책의 독서 기록을 저장합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "독서 기록 저장 성공")
    })
    @PostMapping
    public ResponseDTO create(@RequestBody RecordReadingLogRequest dto,
                              @AuthenticationPrincipal CustomUserDetails user) {

        long userId = user.getUser().getId();

        reportService.RecordReadingLog(dto, userId);

        return new ResponseDTO<>(
                "readingLog successfully recorded"
        );
    }

    @Operation(
            summary = "독서 기록 조회하기",
            description = "책장에 있는 책의 독서 기록을 년도/월에 맞게 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자신의 독서 기록 조회 성공")
    })
    @GetMapping
    public ResponseDTO<List<ReadingLogResponse>> read(@AuthenticationPrincipal CustomUserDetails user) {
        long userId = user.getUser().getId();
        List<ReadingLogResponse> response = reportService.getReadingLog(userId);

        return new ResponseDTO<>(
                response
        );
    }

    @Operation(
            summary = "다른 유저의 독서 기록 조회하기",
            description = "다른 유저의 독서 기록을 조회할 수 있습니다. 다른 유저의 상태가 비공개이면서 친구 관계가 아니라면 F403을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저의 독서 기록 조회 성공")
    })
    @GetMapping("/others")
    public ResponseDTO<ReadingLogResponseWithTheme> userLog(@RequestParam long otherId,
                                                            @AuthenticationPrincipal CustomUserDetails user) {

        long userId = user.getUser().getId();
        ReadingLogResponseWithTheme response = reportService.getReadingLog(userId, otherId);

        return new ResponseDTO<>(
                response
        );
    }

    @Operation(
            summary = "리포트 조회하기",
            description = "저번 달 케릭터와 독서 기록이 담긴 리포트를 조회합니다. 만약 케릭터에 대응되는 것이 비었다면 empty_data를 반환합니다." +
                    "완독이 비었다면 data에 EMPTY_FINISH, 기록이 비었다면 logData에 EMPTY_LOG를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "독서 리포트 조회 성공")
    })
    @GetMapping("/monthly")
    public ResponseDTO<ReportResponse> getReport(@AuthenticationPrincipal CustomUserDetails user) {
        long userId = user.getUser().getId();
        ReportResponse response = reportService.getReportData(userId);

        return new ResponseDTO<>(
                response
        );
    }
}
