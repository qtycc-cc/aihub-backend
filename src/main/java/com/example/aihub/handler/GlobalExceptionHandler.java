package com.example.aihub.handler;

import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.aihub.exception.InvalidCredentialsException;
import com.example.aihub.utils.JsonUtils;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public ResponseEntity<?> handleDuplicateKeyException(DuplicateKeyException e) {
        return ResponseEntity.internalServerError().body(JsonUtils.toJson(
            Map.of("reason", e.getMessage())
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(JsonUtils.toJson(
            Map.of("reason", e.getMessage())
        ));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseBody
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return ResponseEntity.badRequest().body(JsonUtils.toJson(
            Map.of("reason", e.getMessage())
        ));
    }
}