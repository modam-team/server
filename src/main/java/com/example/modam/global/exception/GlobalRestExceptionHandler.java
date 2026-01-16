package com.example.modam.global.exception;

import com.example.modam.global.response.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.CompletionException;

@RestControllerAdvice
public class GlobalRestExceptionHandler {
    @ExceptionHandler(value = {ApiException.class})
    public ResponseEntity<?> handleApiException(ApiException e) {
        return ResponseDTO.toResponseEntity(e);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleInvalidArgumentException(MethodArgumentNotValidException e) {
        return ResponseDTO.toResponseEntity(e);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Object> handleJSONConversionException(HttpMessageConversionException e) {
        return ResponseDTO.toResponseEntity(e);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseDTO.toResponseEntity(e);
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<?> handleCompletionException(CompletionException e) {
        Throwable cause = e.getCause();

        if (cause instanceof ApiException apiException) {
            return ResponseDTO.toResponseEntity(apiException);
        }

        return ResponseDTO.toResponseEntity(e);
    }

}