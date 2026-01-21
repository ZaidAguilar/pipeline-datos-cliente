package com.zaid.examen.exception;

import com.zaid.examen.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<ApiResponseDto<Object>> handleAny(Exception ex) {
        ApiResponseDto<Object> resp = ApiResponseDto.builder()
                .codigo("EXAMEN.GENERAL.ERROR")
                .id(UUID.randomUUID().toString())
                .mensaje(ex.getMessage() == null ? "Unexpected error" : ex.getMessage())
                .respuesta(null)
                .build();

        return org.springframework.http.ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}