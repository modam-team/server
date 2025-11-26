package com.example.modam.domain.review.Presentation;

import com.example.modam.domain.review.Application.ReviewService;
import com.example.modam.global.response.ResponseDTO;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/review")
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(
            summary = "리뷰 생성",
            description = "로그인한 사용자가 리뷰 내용을 입력하여 새로운 리뷰를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 생성 성공")
    })
    @PostMapping
    public ResponseDTO create(
            @RequestBody ReviewRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails user) {

        long userId = user.getUser().getId();

        reviewService.saveReview(userId, dto);

        return new ResponseDTO(
                "Review Successfully created"
        );

    }
}
