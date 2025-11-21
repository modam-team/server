package com.example.modam.book;

import com.example.modam.domain.book.BookDataService;
import com.example.modam.domain.book.BookEntity;
import com.example.modam.domain.book.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookDataTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookDataService bookDataService;

    @Test
    @DisplayName("DB에 중복 책을 저장하지 않는 테스트입니다.")
    void book_save_test() {
        BookEntity b1 = new BookEntity(1, "황록1", "하하하", "황", "소설/문학", "a", "123");
        BookEntity b2 = new BookEntity(2, "황록2", "하하하", "황", "소설/문학", "a", "456");
        BookEntity b3 = new BookEntity(3, "황록3", "하하하", "황", "소설/문학", "a", "789");
        List<BookEntity> input = List.of(b1, b2, b3);
        when(bookRepository.findAllByItemIdIn(List.of("123", "456", "789")))
                .thenReturn(List.of(b2));

        bookDataService.saveBook(input);

        // captor를 통해 내부에 저장된 데이터를 관측. 디버깅 시에 활용하자
        ArgumentCaptor<List<BookEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(bookRepository, times(1)).saveAll(captor.capture());
        List<BookEntity> saved = captor.getValue();
        Set<String> savedItemIds = saved.stream()
                .map(BookEntity::getItemId)
                .collect(Collectors.toSet());

        assertThat(savedItemIds).containsExactlyInAnyOrder("123", "789");
        assertThat(savedItemIds).doesNotContain("456");

        verify(bookRepository, times(1)).findAllByItemIdIn(List.of("123", "456", "789"));
        verifyNoMoreInteractions(bookRepository);
    }
}
