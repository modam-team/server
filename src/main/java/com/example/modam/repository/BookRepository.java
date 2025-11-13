package com.example.modam.repository;

import com.example.modam.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

// Book 엔티티와 해당 엔티티의 Primary Key 타입 지정
public interface BookRepository extends JpaRepository<Book, Long>{
    
}