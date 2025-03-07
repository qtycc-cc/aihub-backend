package com.example.aihub.handler;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.aihub.exception.InvalidCredentialsException;
import com.example.aihub.pojo.ExceptionResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleDuplicateKeyException(DuplicateKeyException e) {
        return ResponseEntity.internalServerError().body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return ResponseEntity.badRequest().body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }
}