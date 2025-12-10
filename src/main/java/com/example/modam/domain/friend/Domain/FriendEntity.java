package com.example.modam.domain.friend.Domain;

import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name="friend")
@EntityListeners(AuditingEntityListener.class)
public class FriendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private UserEntity requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void acceptRequest(){
        if (this.status == FriendStatus.PENDING){
            this.status = FriendStatus.ACCEPTED;
        } else {
            throw new ApiException(ErrorDefine.FRIEND_REQUEST_NOT_FOUND);
        }
    }

    public void rejectRequest(){
        if (this.status == FriendStatus.PENDING){
            this.status = FriendStatus.REJECTED;
        } else {
            throw new ApiException(ErrorDefine.FRIEND_REQUEST_NOT_FOUND);
        }
    }
}
