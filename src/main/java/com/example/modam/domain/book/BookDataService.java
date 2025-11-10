package com.example.modam.domain.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookDataService {

    private final BookRepository bookRepository;

    public BookDataService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void saveBook(List<BookEntity> data) {
        List<String> ids = data.stream()
                .map(BookEntity::getId)
                .collect(Collectors.toList());

        List<BookEntity> existing = bookRepository.findAllById(ids);

        Set<String> existingIds = existing.stream()
                .map(BookEntity::getId)
                .collect(Collectors.toSet());

        List<BookEntity> toSave = data.stream()
                .filter(b -> !existingIds.contains(b.getId()))
                .collect(Collectors.toList());

        if (!toSave.isEmpty()) {
            bookRepository.saveAll(toSave);
        }
    }
}
