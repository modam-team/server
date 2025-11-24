package com.example.modam.global.security.jwt;

import com.example.modam.global.exception.ErrorDefine;
import com.example.modam.global.response.ExceptionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        setResponse(response, ErrorDefine.TOKEN_INVALID);
    }

    private void setResponse(HttpServletResponse response, ErrorDefine errorDefine) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorDefine.getHttpStatus().value());

        ExceptionDTO dto = new ExceptionDTO(errorDefine);
        String errorJson = objectMapper.writeValueAsString(dto);

        response.getWriter().write(errorJson);
    }

}