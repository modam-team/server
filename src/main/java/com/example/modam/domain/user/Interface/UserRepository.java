package com.example.modam.domain.user.Interface;

import com.example.modam.domain.user.Domain.UserEntity;
import com.example.modam.domain.user.Domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByProviderId(String providerId);

    // 닉네임으로 UserEntity를 찾음
    Optional<UserEntity> findByNickname(String nickname);

    // 닉네임 존재 여부 확인
    boolean existsByNickname(String nickname);

    @Query("""
            select u.preferredCategories
            from user u
            where u.id = :userId
            """)
    String findUserCategory(@Param("userId") long userId);

    List<UserEntity> findAllByStatusAndWithdrawalRequestedAtBefore(
            UserStatus status,
            LocalDateTime cutoffTime
    );
}