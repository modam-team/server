package com.example.modam.global.response;

import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.InvalidateArgumentExceptionDTO;
import com.example.modam.global.exception.JSONConvertExceptionDTO;
import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@Builder
@AllArgsConstructor
public class ResponseDTO<T> {
    private boolean isSuccess;
    private final T responseDto;
    private ExceptionDTO error;

    public ResponseDTO(@Nullable T responseDto) {
        this.isSuccess = true;
        this.responseDto = responseDto;
    }

    public static ResponseEntity<?> toResponseEntity(ApiException e) {
        return ResponseEntity.status(e.getError().getHttpStatus())
                .body(
                        ResponseDTO.builder()
                                .isSuccess(false)
                                .responseDto(null)
                                .error(new ExceptionDTO(e.getError()))
                                .build());
    }

    public static ResponseEntity<Object> toResponseEntity(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ResponseDTO.builder()
                                .isSuccess(false)
                                .responseDto(null)
                                .error(new InvalidateArgumentExceptionDTO(e))
                                .build());
    }

    public static ResponseEntity<Object> toResponseEntity(HttpMessageConversionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ResponseDTO.builder()
                                .isSuccess(false)
                                .responseDto(null)
                                .error(new JSONConvertExceptionDTO(e))
                                .build());
    }

    public static ResponseEntity<Object> toResponseEntity(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ResponseDTO.builder()
                                .isSuccess(false)
                                .responseDto(null)
                                .error(new ExceptionDTO(e))
                                .build());
    }
}