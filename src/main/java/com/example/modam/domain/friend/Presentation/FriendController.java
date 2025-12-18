package com.example.modam.domain.friend.Presentation;

import com.example.modam.domain.friend.Application.FriendService;
import com.example.modam.domain.friend.Presentation.dto.FriendRequestDto;
import com.example.modam.domain.friend.Presentation.dto.FriendSearchResponse;
import com.example.modam.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Friend", description = "친구 관련 API")
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

    @Operation(
            summary = "친구 목록 조회",
            description = "현재 로그인된 사용자의 활성(accepted)친구 목록을 조회합니다"
    )
    @GetMapping
    public ResponseEntity<List<FriendSearchResponse>> getFriendList(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long currentUserId = user.getUser().getId();
        List<FriendSearchResponse> friendList = friendService.getFriendList(currentUserId);
        return ResponseEntity.ok(friendList);
    }

    @Operation(
            summary="친구 요청 거절",
            description="로그인한 사용자가 받은 친구 요청을 거절합니다."
    )
    @DeleteMapping("/request/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @Valid @RequestBody FriendRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        Long currentUserId = user.getUser().getId();
        Long requesterId = requestDto.getTargetUserId();

        friendService.rejectFriendRequest(requesterId, currentUserId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "내가 보낸 요청 취소",
            description = "로그인한 사용자가 상대방에게 보낸 PENDING 상태의 요청을 취소하고 물리적으로 삭제합니다"
    )
    @DeleteMapping("/request/cancel")
    public ResponseEntity<Void> cancelFriendRequest(
            @Valid @RequestBody FriendRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        Long currentUserId = user.getUser().getId();
        Long receiverId = requestDto.getTargetUserId();

        friendService.cancelFriendRequest(receiverId, currentUserId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "친구 관계 삭제",
            description = "ACCEPTED 상태의 친구 관계를 끊습니다"
    )
    @DeleteMapping("/unfriend")
    public ResponseEntity<Void> deleteFriendship(
            @Valid @RequestBody FriendRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        Long currentUserId = user.getUser().getId();
        Long targetUserId = requestDto.getTargetUserId();

        friendService.deleteFriendship(targetUserId, currentUserId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "받은 친구 요청 목록 조회",
            description = "현재 로그인한 사용자에게 들어온 PENDING 상태의 친구 요청 목록을 조회합니다."
    )
    @GetMapping("/requests/received")
    public ResponseEntity<List<FriendSearchResponse>> getReceivedRequests(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long currentUserId = user.getUser().getId();
        List<FriendSearchResponse> receivedRequests = friendService.getReceivedRequestList(currentUserId);
        return ResponseEntity.ok(receivedRequests);
    }
}
