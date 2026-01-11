package com.example.modam.domain.user.Domain;

import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(nullable = true)
    private LocalDateTime withdrawalRequestedAt;

    @Column(nullable = false)
    @Builder.Default
    private String themeColor="#608540";

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public void updateProfileImageUrl(String imageUrl){
        this.profileImageUrl = imageUrl;
    }

    public void updateOnboardingInfo(String nickname, Integer goalScore, List<String> categories){
        this.nickname = nickname;
        this.goalScore = goalScore;
        this.isOnboardingCompleted = true;
        this.preferredCategories = String.join(",", categories);
    }

    public void updateProfileInfo(String nickname, Boolean isPublic, Integer goalScore){
        if (nickname!=null){
            this.nickname = nickname;
        }
        if (isPublic!=null){
            this.isPublic = isPublic;
        }
        if (goalScore!=null){
            this.goalScore = goalScore;
        }
    }

    public void updateThemeColor(String themeColor){
        if (themeColor == null || themeColor.isBlank()){
            throw new ApiException(ErrorDefine.INVALID_THEME_COLOR);
        }
        if (!themeColor.startsWith("#")){
            this.themeColor = "#" + themeColor;
        } else {
            this.themeColor = themeColor;
        }
    }

    public void updatePreferredCategories(List<String> categories){
        this.preferredCategories = String.join(",",categories);
    }

    public void requestWithdrawal(){
        this.status = UserStatus.WITHDRAWAL_PENDING;
        this.withdrawalRequestedAt = LocalDateTime.now();
    }

    public void activateAccount(){
        this.status = UserStatus.ACTIVE;
        this.withdrawalRequestedAt = null;
    }
}

