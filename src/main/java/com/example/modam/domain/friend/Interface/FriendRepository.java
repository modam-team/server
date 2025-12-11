package com.example.modam.domain.friend.Interface;

import com.example.modam.domain.friend.Domain.FriendEntity;
import com.example.modam.domain.friend.Domain.FriendStatus;
import com.example.modam.domain.user.Domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<FriendEntity, Long> {
    // 요청 상태 확인
    Optional<FriendEntity> findByRequesterAndReceiverAndStatus(
            Long requesterId,
            Long receiverId,
            FriendStatus status
    );

    // 친구 관계 확인
    @Query("SELECT f FROM FriendEntity f WHERE " +
            "(f.requester.id = :user1Id AND f.receiver.id = :user2Id AND f.status = 'ACCEPTED') OR " +
            "(f.requester.id = :user2Id AND f.receiver.id = :user1Id AND f.status = 'ACCEPTED')")
    Optional<FriendEntity> isFriendshipEstablished(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );


    // 친구 목록 조회
    List<FriendEntity> findAllByRequesterIdAndStatusOrReceiverIdAndStatus(
            Long requesterId, FriendStatus requesterStatus,
            Long receiverId, FriendStatus receiverStatus
    );

    @Query("SELECT f FROM FriendEntity f WHERE " +
                  "(f.requester.id = :userId OR f.receiver.id = :userId) AND f.status = 'ACCEPTED'")
    List<FriendEntity> findAllFriendsByUserId(@Param("userId") Long userId);

}
