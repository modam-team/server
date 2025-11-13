package com.example.modam.repository;

import com.example.modam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

//User 엔티티와 해당 엔티티의 Primary Key 타입 지정
public interface UserRepository extends JpaRepository<User,Long>{
    // JpaRepository를 상속하는 것만으로 User 테이블에 대한 
    // save(), findById(), findAll() 등의 메서드가 자동으로 제공됩니다.
}

