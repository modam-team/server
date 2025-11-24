package com.example.modam.domain.book.Interface;

import com.example.modam.domain.book.Domain.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    List<BookEntity> findAllByItemIdIn(List<String> itemIds);
}
