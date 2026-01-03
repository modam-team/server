package com.example.modam.book;

import com.example.modam.domain.book.Presentation.dto.AladinResponse;
import com.example.modam.domain.book.Application.BookDataService;
import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.domain.book.Interface.BookRepository;
import com.example.modam.domain.book.Presentation.dto.addBookRequest;
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
        AladinResponse b1 = new AladinResponse("123", "황록1", "하하하", "황", "소설/문학", "a","b");
        AladinResponse b2 = new AladinResponse("456", "황록1", "하하하", "황", "소설/문학", "a","b");
        AladinResponse b3 = new AladinResponse("789", "황록1", "하하하", "황", "소설/문학", "a","b");
        List<AladinResponse> input = List.of(b1, b2, b3);

        List<String> ids = List.of("123", "456", "789");

        // 기존 DB에 이미 존재한다고 가정하는 엔티티는 b2만 존재하도록 설정
        when(bookRepository.findAllByItemIdIn(ids))
                .thenReturn(List.of(BookEntity.toDatabase(b2)));

        bookDataService.saveBook(input);

        // saveAll에 저장된 값 캡처
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<BookEntity>> captor = (ArgumentCaptor) ArgumentCaptor.forClass(List.class);
        verify(bookRepository, times(1)).saveAll(captor.capture());
        List<BookEntity> saved = captor.getValue();

        Set<String> savedItemIds = saved.stream()
                .map(BookEntity::getItemId)
                .collect(Collectors.toSet());

        assertThat(savedItemIds).containsExactlyInAnyOrder("123", "789");
        assertThat(savedItemIds).doesNotContain("456");

        // 핵심: findAllByItemIdIn은 서비스에서 두 번 호출되므로 times(2)
        verify(bookRepository, times(2)).findAllByItemIdIn(ids);

        // 더 이상의 호출이 없음을 확인
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("사용자 요청이 올바르게 저장됐는지 확인하는 테스트")
    void user_book_request() throws Exception {

        addBookRequest dto = new addBookRequest();

        java.lang.reflect.Field title = addBookRequest.class.getDeclaredField("title");
        java.lang.reflect.Field author = addBookRequest.class.getDeclaredField("author");
        java.lang.reflect.Field publisher = addBookRequest.class.getDeclaredField("publisher");
        java.lang.reflect.Field category = addBookRequest.class.getDeclaredField("category");

        title.setAccessible(true);
        author.setAccessible(true);
        publisher.setAccessible(true);
        category.setAccessible(true);

        title.set(dto, "황록의 전설");
        author.set(dto, "황록");
        publisher.set(dto, "모담");
        category.set(dto, "소설");

        BookEntity savedEntity = BookEntity.builder()
                .title("황록의 전설")
                .author("황록")
                .publisher("모담")
                .categoryName("소설")
                .receivedFromAladin(false)
                .build();

        when(bookRepository.save(any(BookEntity.class))).thenReturn(savedEntity);

        BookEntity result = bookDataService.requestBook(dto);

        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository).save(captor.capture());

        BookEntity captured = captor.getValue();

        assertThat(captured.getTitle()).isEqualTo("황록의 전설");
        assertThat(captured.getAuthor()).isEqualTo("황록");
        assertThat(captured.getPublisher()).isEqualTo("모담");
        assertThat(captured.getCategoryName()).isEqualTo("소설");
        assertThat(captured.isReceivedFromAladin()).isFalse();

        verifyNoMoreInteractions(bookRepository);
    }
}
