package com.example.modam.book;

import com.example.modam.domain.book.BookService;
import com.example.modam.domain.book.CategoryMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AladinTest {

    @Mock
    private CategoryMapping categoryMapping;

    @InjectMocks
    private BookService bookService;

    @DisplayName("서비스에 활용안할 카테고리를 치운다.")
    @Test
    void test_set_impossible(){
        String input="국내도서>고등학교참고서>고등-문제집>영어영역";

        when(categoryMapping.isImpossible("고등학교참고서")).thenReturn(true);
        String result = bookService.preprocessCategory(input); // impossible category

        assertThat(result).isEqualTo("impossible category");
        verify(categoryMapping).isImpossible("고등학교참고서");
        verifyNoMoreInteractions(categoryMapping);
    }

    @DisplayName("서비스에 활용할 17개의 카테고리 중 하나로 교체한다.")
    @Test
    void test_map_transaction(){
        String input="국내도서>소설/시/희곡>추리/미스터리소설>영미 추리/미스터리소설";

        when(categoryMapping.isImpossible("소설/시/희곡")).thenReturn(false);
        when(categoryMapping.mapCategory("소설/시/희곡")).thenReturn("소설/문학");
        String result= bookService.preprocessCategory(input);

        assertThat(result).isEqualTo("소설/문학");
        verify(categoryMapping).mapCategory("소설/시/희곡");
        verifyNoMoreInteractions(categoryMapping);
    }
}
