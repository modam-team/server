package com.example.modam.domain.friend.Presentation;

import com.amazonaws.Response;
import com.example.modam.domain.friend.Application.FriendService;
import com.example.modam.domain.friend.Presentation.dto.FriendRequestDto;
import com.example.modam.domain.friend.Presentation.dto.FriendSearchResponse;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    @Operation(
            summary = "친구 요청 보내기",
            description = "로그인한 사용자가 특정 사용자에게 친구 요청을 보냅니다."
    )
    @PostMapping("/request")
    public ResponseEntity<Void> sendFriendRequest(
            @Valid @RequestBody FriendRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        Long currentUserId = user.getUser().getId();
        friendService.sendFriendRequest(requestDto.getTargetUserId(), currentUserId);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "친구 요청 수락하기",
            description = "로그인한 사용자가 받은 친구 요청을 수락하여 친구 관계를 성립합니다."
    )
    @PostMapping("/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @Valid @RequestBody FriendRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        Long currentUserId = user.getUser().getId();
        Long requestId = requestDto.getTargetUserId();

        friendService.acceptFriendRequest(requestId, currentUserId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "닉네임으로 친구 검색",
            description = "닉네임으로 사용자를 검색하고, 현재 로그인 사용자 기준 관계 상태를 함께 반환합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<List<FriendSearchResponse>> searchFriends(
            @RequestParam String nickname,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long currentUserId = user.getUser().getId();
        List<FriendSearchResponse> results = friendService.searchUsersByNickname(nickname, currentUserId);
        return ResponseEntity.ok(results);
    }
}
