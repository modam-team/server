package com.example.modam.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Indexed;

import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED) // JPA 사용을 위한 기본 생성자
@Entity
@Table(name="User")
public class User {
    // 사용자 id (pk)
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // Auto Increment 설정
    @Column(name="id", columnDefinition="long")
    private Long id;

    //사용자 이름
    @Column(name="name",nullable=false, length=255)
    private String name;

    //사용자 이메일 (고유값)
    @Column(name="email", nullable=false, length=255, unique=true)
    private String email;

    //사용자 비밀번호
    @Column(name="password", nullable=false, length=255)
    private String password;

    //객체 생성을 위한 빌더 패턴 생성자
    @Builder
    publilc User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
