package com.example.modam.domain.user;

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

    @Column(nullable = false)
    private String name;

    // 카카오 이메일 미제공 (필수x)
    @Column
    private String email;

    // 카카오가 주는 고유 ID(숫자)
    @Column(nullable = false, unique = true)
    private String providerId;
}

