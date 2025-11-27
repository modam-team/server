package com.example.modam.domain.user.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 사용자 이름 (카카오에서 받은 기본 이름)
    @Column(nullable = false)
    private String name;

    // 카카오가 주는 고유 ID(숫자)
    @Column(nullable = false, unique = true)
    private String providerId;

    // 닉네임
    @Column(nullable = true, unique = true)
    private String nickname;

    // 목표 점수 (온보딩 전에는 NULL 허용)
    @Column(nullable = true)
    private Integer goalScore;

    // 카테고리
    @Column(nullable = true, length = 500)
    private String preferredCategories;

    // 온보딩 완료 플래그
    @Column(nullable = false)
    @Builder.Default
    private boolean isOnboardingCompleted = false;

    public void updateOnboardingInfo(String nickname, Integer goalScore, List<String> categories){
        this.nickname = nickname;
        this.goalScore = goalScore;
        this.isOnboardingCompleted = true;
        this.preferredCategories = String.join(",", categories);
    }
}

