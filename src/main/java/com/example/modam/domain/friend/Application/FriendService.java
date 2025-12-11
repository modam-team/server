package com.example.modam.domain.friend.Application;

import com.example.modam.domain.friend.Domain.FriendEntity;
import com.example.modam.domain.friend.Domain.FriendStatus;
import com.example.modam.domain.friend.Interface.FriendRepository;
import com.example.modam.domain.friend.Presentation.dto.FriendRelationStatus;
import com.example.modam.domain.friend.Presentation.dto.FriendSearchResponse;
import com.example.modam.domain.user.Application.UserService;
import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserService userService;

    @Transactional
    public void sendFriendRequest(Long targetUserId, Long currentUserId){
        if (targetUserId.equals(currentUserId)){
            throw new ApiException(ErrorDefine.SELF_FRIEND_REQUEST);
        }

        UserEntity requester = userService.findUserById(currentUserId);
        UserEntity receiver = userService.findUserById(targetUserId);

        if (friendRepository.isFriendshipEstablished(currentUserId, targetUserId).isPresent()){
            throw new ApiException(ErrorDefine.FRIEND_ALREADY_REQUESTED);
        }

        // 상대가 나한테 요청을 보낸 적이 있는지
        Optional<FriendEntity> receivedRequest = friendRepository.findByRequesterIdAndReceiverIdAndStatus(
                receiver.getId(), requester.getId(), FriendStatus.PENDING
        );
        // "친구가 이미 요청을 보냈으므로 수락하세요" 라는 메시지를 보내도록 중복 요청 처리하였음
        if (receivedRequest.isPresent()){
            throw new ApiException(ErrorDefine.FRIEND_ALREADY_REQUESTED);
        }

        FriendEntity newRequest = FriendEntity.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendStatus.PENDING)
                .build();

        friendRepository.save(newRequest);
    }

    @Transactional
    public void acceptFriendRequest(Long requesterId, Long currentUserId) {
        FriendEntity friendRequest = friendRepository
                .findByRequesterIdAndReceiverIdAndStatus(
                        requesterId,
                        currentUserId,
                        FriendStatus.PENDING
                )
                .orElseThrow(() -> new ApiException(ErrorDefine.FRIEND_REQUEST_NOT_FOUND));

        friendRequest.acceptRequest();
        friendRepository.save(friendRequest);
    }

    public List<FriendSearchResponse> searchUsersByNickname(String nickname, Long currentUserId){
        String searchKeyword = nickname + "*";
        List<UserEntity> searchResults = userService.findUsersByNicknameFullTextSearch(searchKeyword);
        UserEntity currentUser = userService.findUserById(currentUserId);

        return searchResults.stream()
                .filter(user -> user.getId()!=currentUserId)
                .map(user -> {
                    FriendRelationStatus status = calculateRelationStatus(currentUser, user);
                    return FriendSearchResponse.from(user, status);
                })
                .collect(Collectors.toList());
    }

    private FriendRelationStatus calculateRelationStatus(UserEntity user1, UserEntity user2) {
        if (friendRepository.isFriendshipEstablished(user1.getId(), user2.getId()).isPresent()) {
            return FriendRelationStatus.FRIENDS;
        }
        Optional<FriendEntity> sentRequest = friendRepository.findByRequesterIdAndReceiverIdAndStatus(
                user1.getId(), user2.getId(), FriendStatus.PENDING
        );
        if (sentRequest.isPresent()) {
            return FriendRelationStatus.REQUEST_SENT;
        }

        Optional<FriendEntity> receivedRequest = friendRepository.findByRequesterIdAndReceiverIdAndStatus(
                user2.getId(), user1.getId(), FriendStatus.PENDING
        );
        if (receivedRequest.isPresent()) {
            return FriendRelationStatus.REQUEST_RECEIVED;
        }

        return FriendRelationStatus.NOT_FRIENDS;
    }

    // 친구 요청 거절
    @Transactional
    public void rejectFriendRequest(Long requesterId, Long currentUserId){
        FriendEntity friendRequest = friendRepository
                .findByRequesterIdAndReceiverIdAndStatus(
                        requesterId,
                        currentUserId,
                        FriendStatus.PENDING
                )
                .orElseThrow(()-> new ApiException(ErrorDefine.FRIEND_REQUEST_NOT_FOUND));

        friendRequest.rejectRequest();
        friendRepository.save(friendRequest);
    }

    // 친구 요청 취소
    @Transactional
    public void cancelFriendRequest(Long receiverId, Long currentUserId){
        FriendEntity friendRequest = friendRepository
                .findByRequesterIdAndReceiverIdAndStatus(
                        currentUserId,
                        receiverId,
                        FriendStatus.PENDING
                )
                .orElseThrow(()-> new ApiException(ErrorDefine.FRIEND_REQUEST_NOT_FOUND));

        friendRepository.delete(friendRequest);
    }

    // 친구 삭제
    @Transactional
    public void deleteFriendship(Long targetUserId, Long currentUserId){
        FriendEntity friendship = friendRepository
                .isFriendshipEstablished(currentUserId, targetUserId)
                .orElseThrow(()-> new ApiException(ErrorDefine.FRIEND_REQUEST_NOT_FOUND));

        friendRepository.delete(friendship);
    }

    // 친구인 목록만 조회
    @Transactional(readOnly = true)
    public List<FriendSearchResponse> getFriendList(Long currentUserId){
        List<FriendEntity> acceptedFriends = friendRepository
                .findAllByRequesterIdAndStatusOrReceiverIdAndStatus(
                        currentUserId, FriendStatus.ACCEPTED,
                        currentUserId, FriendStatus.ACCEPTED
                );

        return acceptedFriends.stream()
                .map(friendship -> {
                    UserEntity friendUser = friendship.getRequester().getId() == currentUserId ?
                            friendship.getReceiver() : friendship.getRequester();
                    return FriendSearchResponse.from(friendUser, FriendRelationStatus.FRIENDS);
                })
                .collect(Collectors.toList());
    }
}
