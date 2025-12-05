package com.example.modam.domain.user.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String providerId;

    @Column(nullable = true, unique = true)
    private String nickname;

    @Column(nullable = true)
    private Integer goalScore;

    @Column(nullable = true, length = 500)
    private String preferredCategories;

    @Column(nullable = false)
    @Builder.Default
    private boolean isOnboardingCompleted = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    @Column(nullable=true)
    private String profileImageUrl;

    public void updateProfileImageUrl(String imageUrl){
        this.profileImageUrl = imageUrl;
    }

    public void updateOnboardingInfo(String nickname, Integer goalScore, List<String> categories){
        this.nickname = nickname;
        this.goalScore = goalScore;
        this.isOnboardingCompleted = true;
        this.preferredCategories = String.join(",", categories);
    }

    public void updateProfileInfo(String nickname, Boolean isPublic){
        this.nickname = nickname;
        this.isPublic = isPublic; // 공개 여부 업데이트
    }
}

