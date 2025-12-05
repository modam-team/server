package com.example.modam.domain.report.Presentation;

import com.example.modam.domain.report.Application.ReportService;
import com.example.modam.domain.report.Presentation.dto.RecordReadingLogRequest;
import com.example.modam.global.response.ResponseDTO;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
