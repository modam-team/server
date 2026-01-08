package com.example.modam.domain.review.Presentation;

import com.example.modam.domain.review.Application.ReviewService;
import com.example.modam.domain.review.Presentation.dto.BookSearchReviewResponse;
import com.example.modam.domain.review.Presentation.dto.ChangeCommentRequest;
import com.example.modam.domain.review.Presentation.dto.ReviewRequestDTO;
import com.example.modam.domain.review.Presentation.dto.ReviewResponse;
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

    @Operation(
            summary = "책장에서 리뷰 조회",
            description = "완독 책장에서 완독된 책의 리뷰 정보를 불러옵니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책장 리뷰 조회 성공")
    })
    @GetMapping
    public ResponseDTO<ReviewResponse> read(@RequestParam long bookId,
                                            @AuthenticationPrincipal CustomUserDetails user) {
        long userId = user.getUser().getId();

        ReviewResponse response = reviewService.readReview(userId, bookId);

        return new ResponseDTO(
                response
        );
    }

    @Operation(
            summary = "다른 사람의 리뷰 조회",
            description = "다른 사람의 히스토리에서 완독 책의 카드를 조회할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다른 사람 리뷰 조회 성공")
    })
    @GetMapping("/other")
    public ResponseDTO<ReviewResponse> readOther(@RequestParam long bookId,
                                                 @RequestParam long otherId,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        long userId = user.getUser().getId();

        ReviewResponse response = reviewService.readReview(otherId, bookId);

        return new ResponseDTO(
                response
        );
    }

    @Operation(
            summary = "책 검색 시 리뷰 조회",
            description = "책 검색하고 나온 책에 대한 다른 유저들의 리뷰 확인"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 리뷰 조회 성공")
    })
    @GetMapping("/search")
    public ResponseDTO<List<BookSearchReviewResponse>> search(@RequestParam long bookId,
                                                              @AuthenticationPrincipal CustomUserDetails user) {
        List<BookSearchReviewResponse> response = reviewService.readBookReview(bookId);

        return new ResponseDTO(
                response
        );
    }

    @Operation(
            summary = "리뷰 코멘트 수정",
            description = "완독 책장에서 리뷰 코멘트를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공")
    })
    @PatchMapping
    public ResponseDTO update(@AuthenticationPrincipal CustomUserDetails user,
                              @RequestBody ChangeCommentRequest dto) {
        long userId = user.getUser().getId();
        reviewService.changeReviewComment(userId, dto);

        return new ResponseDTO(
                "Comment Successfully changed"
        );
    }
}
