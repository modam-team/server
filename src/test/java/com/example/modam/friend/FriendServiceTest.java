package com.example.modam.friend;

import com.example.modam.domain.friend.Application.FriendService;
import com.example.modam.domain.friend.Domain.FriendEntity;
import com.example.modam.domain.friend.Domain.FriendStatus;
import com.example.modam.domain.friend.Interface.FriendRepository;
import com.example.modam.domain.user.Application.UserService;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendService friendService;

    private final Long USER_ID = 1L;
    private final Long TARGET_ID = 2L;

    private UserEntity mockUser(Long id) {
        return UserEntity.builder().id(id).build();
    }

    @DisplayName("친구 요청 성공 테스트")
    @Test
    void sendFriendRequest_Success() {
        UserEntity requester = mockUser(USER_ID);
        UserEntity receiver = mockUser(TARGET_ID);

        when(userService.findUserById(USER_ID)).thenReturn(requester);
        when(userService.findUserById(TARGET_ID)).thenReturn(receiver);
        when(friendRepository.findAnyRelationBetweenUsers(USER_ID, TARGET_ID)).thenReturn(Optional.empty());

        friendService.sendFriendRequest(TARGET_ID, USER_ID);
        verify(friendRepository, times(1)).save(any(FriendEntity.class));
    }

    @DisplayName("친구 요청 실패 (자기 자신) 테스트")
    @Test
    void sendFriendRequest_SelfRequest() {
        ApiException exception = assertThrows(ApiException.class,
                () -> friendService.sendFriendRequest(USER_ID, USER_ID));

        assertEquals(ErrorDefine.SELF_FRIEND_REQUEST, exception.getError());
        verify(friendRepository, never()).save(any());
    }

    @Test
    @DisplayName("친구 요청 실패 (이미 관계가 존재할 경우)")
    void sendFriendRequest_AlreadyRequested() {
        when(friendRepository.findAnyRelationBetweenUsers(USER_ID, TARGET_ID))
                .thenReturn(Optional.of(mock(FriendEntity.class))); // 관계가 이미 있음을 Mocking

        Assertions.assertThrows(ApiException.class, () -> {
            friendService.sendFriendRequest(TARGET_ID, USER_ID);
        });

    }

    @DisplayName("요청 수락 성공 테스트")
    @Test
    void acceptFriendRequest_Success() {
        Long requesterId = TARGET_ID;
        Long receiverId = USER_ID;

        FriendEntity pendingRequest = spy(FriendEntity.builder()
                .status(FriendStatus.PENDING)
                .build());

        when(friendRepository.findByRequesterIdAndReceiverIdAndStatus(
                requesterId, receiverId, FriendStatus.PENDING))
                .thenReturn(Optional.of(pendingRequest));

        friendService.acceptFriendRequest(requesterId, receiverId);

        verify(pendingRequest, times(1)).acceptRequest();
        verify(friendRepository, times(1)).save(pendingRequest);
    }

    @DisplayName("요청 수락 실패 (요청 없음) 테스트")
    @Test
    void acceptFriendRequest_NotFound() {
        Long requesterId = TARGET_ID;
        Long receiverId = USER_ID;

        when(friendRepository.findByRequesterIdAndReceiverIdAndStatus(
                requesterId, receiverId, FriendStatus.PENDING))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> friendService.acceptFriendRequest(requesterId, receiverId));

        assertEquals(ErrorDefine.FRIEND_REQUEST_NOT_FOUND, exception.getError());
        verify(friendRepository, never()).save(any());
    }

    @DisplayName("요청 거절 성공 테스트")
    @Test
    void rejectFriendRequest_Success() {
        Long requesterId = TARGET_ID;
        Long receiverId = USER_ID;

        FriendEntity pendingRequest = mock(FriendEntity.class);

        when(friendRepository.findByRequesterIdAndReceiverIdAndStatus(
                requesterId, receiverId, FriendStatus.PENDING))
                .thenReturn(Optional.of(pendingRequest));

        friendService.rejectFriendRequest(requesterId, receiverId);

        verify(friendRepository, times(1)).delete(pendingRequest);
        verify(friendRepository, never()).save(any());
    }

    @DisplayName("요청 거절 실패 (요청 없음) 테스트")
    @Test
    void rejectFriendRequest_NotFound() {
        Long requesterId = TARGET_ID;
        Long receiverId = USER_ID;

        when(friendRepository.findByRequesterIdAndReceiverIdAndStatus(
                requesterId, receiverId, FriendStatus.PENDING))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> friendService.rejectFriendRequest(requesterId, receiverId));
        assertEquals(ErrorDefine.FRIEND_REQUEST_NOT_FOUND, exception.getError());
        verify(friendRepository, never()).save(any());
    }

    @DisplayName("요청 취소 성공 (물리적 삭제) 테스트")
    @Test
    void cancelFriendRequest_Success() {
        Long requesterId = USER_ID; // 내가 요청자
        Long receiverId = TARGET_ID;

        FriendEntity pendingRequest = mock(FriendEntity.class);

        when(friendRepository.findByRequesterIdAndReceiverIdAndStatus(
                requesterId, receiverId, FriendStatus.PENDING))
                .thenReturn(Optional.of(pendingRequest));

        friendService.cancelFriendRequest(receiverId, requesterId);
        verify(friendRepository, times(1)).delete(pendingRequest);
    }

    @DisplayName("요청 취소 실패 (요청 없음) 테스트")
    @Test
    void cancelFriendRequest_NotFound() {
        Long requesterId = USER_ID;
        Long receiverId = TARGET_ID;

        when(friendRepository.findByRequesterIdAndReceiverIdAndStatus(
                requesterId, receiverId, FriendStatus.PENDING))
                .thenReturn(Optional.empty());
        ApiException exception = assertThrows(ApiException.class,
                () -> friendService.cancelFriendRequest(receiverId, requesterId));
        assertEquals(ErrorDefine.FRIEND_REQUEST_NOT_FOUND, exception.getError());
        verify(friendRepository, never()).delete(any());
    }

    @DisplayName("친구 관계 삭제 성공 (Unfriend) 테스트")
    @Test
    void deleteFriendship_Success() {
        Long user1Id = USER_ID;
        Long user2Id = TARGET_ID;

        FriendEntity acceptedFriendship = mock(FriendEntity.class);

        when(friendRepository.findAcceptedFriendship(user1Id, user2Id))
                .thenReturn(Optional.of(acceptedFriendship));

        friendService.deleteFriendship(user2Id, user1Id); // user2Id가 targetId, user1Id가 currentId
        verify(friendRepository, times(1)).findAcceptedFriendship(user1Id, user2Id);
        verify(friendRepository, times(1)).delete(acceptedFriendship);
    }

    @DisplayName("친구 관계 삭제 실패 (관계 없음) 테스트")
    @Test
    void deleteFriendship_NotFound() {
        Long user1Id = USER_ID;
        Long user2Id = TARGET_ID;

        when(friendRepository.findAcceptedFriendship(user1Id, user2Id))
                .thenReturn(Optional.empty());
        ApiException exception = assertThrows(ApiException.class,
                () -> friendService.deleteFriendship(user2Id, user1Id));
        assertEquals(ErrorDefine.FRIEND_REQUEST_NOT_FOUND, exception.getError());
        verify(friendRepository, never()).delete(any());
    }


    @DisplayName("친구 목록 조회 성공 테스트")
    @Test
    void getFriendList_Success() {
        Long currentUserId = USER_ID;
        UserEntity user1 = mockUser(USER_ID);
        UserEntity user2 = mockUser(TARGET_ID);
        FriendEntity friend1 = FriendEntity.builder().requester(user1).receiver(user2).build();
        List<FriendEntity> acceptedFriends = List.of(friend1);
        when(friendRepository.findAllByRequesterIdAndStatusOrReceiverIdAndStatus(
                currentUserId, FriendStatus.ACCEPTED,
                currentUserId, FriendStatus.ACCEPTED
        )).thenReturn(acceptedFriends);
        friendService.getFriendList(currentUserId);
        verify(friendRepository, times(1)).findAllByRequesterIdAndStatusOrReceiverIdAndStatus(
                currentUserId, FriendStatus.ACCEPTED,
                currentUserId, FriendStatus.ACCEPTED
        );
    }
}