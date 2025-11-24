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
    public List<BookEntity> saveBook(List<AladinResponse> book) {

        List<BookEntity> data = book.stream()
                .map(BookEntity::toDatabase)
                .collect(Collectors.toList());

        List<String> ids = data.stream()
                .map(BookEntity::getItemId)
                .collect(Collectors.toList());

        List<BookEntity> existing = bookRepository.findAllByItemIdIn(ids);

        Set<String> existingIds = existing.stream()
                .map(BookEntity::getItemId)
                .collect(Collectors.toSet());

        List<BookEntity> toSave = data.stream()
                .filter(b -> !existingIds.contains(b.getItemId()))
                .collect(Collectors.toList());

        if (!toSave.isEmpty()) {
            bookRepository.saveAll(toSave);
        }

        List<BookEntity> response = bookRepository.findAllByItemIdIn(ids);

        return response;
    }
}
