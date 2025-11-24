package com.example.modam.global.security.jwt;

import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.response.ExceptionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public ExceptionFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ApiException e) {
            setCustomErrorResponse(response, e.getError());
        } catch (Exception e) {
            setErrorResponse(response, e);
        }
    }

    // 커스텀 에러에서 불러와줌
    public void setCustomErrorResponse(HttpServletResponse response, ErrorDefine errorDefine) {
        try {
            response.setStatus(errorDefine.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ExceptionDTO dto = new ExceptionDTO(errorDefine);
            objectMapper.writeValue(response.getWriter(), dto);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // HTTP 에러에서 불러와줌
    private void setErrorResponse(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", e.getMessage());

            objectMapper.writeValue(response.getWriter(), body);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}