package com.example.aihub.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.aihub.exception.AccountHasBeenUsedException;
import com.example.aihub.exception.BussinessException;
import com.example.aihub.exception.InvalidCredentialsException;
import com.example.aihub.exception.ModelNotEqualException;
import com.example.aihub.exception.MyIllegalArgumentException;
import com.example.aihub.exception.PermissionDeniedException;
import com.example.aihub.pojo.ExceptionResponse;

import cn.dev33.satoken.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountHasBeenUsedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleAccountHasBeenUsedException(AccountHasBeenUsedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }

    @ExceptionHandler(MyIllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleMyIllegalArgumentException(MyIllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleInvalidCredentialsException(InvalidCredentialsException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }

    @ExceptionHandler(PermissionDeniedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handlePermissionDeniedException(PermissionDeniedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }

    @ExceptionHandler(ModelNotEqualException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleModelNotEqualException(ModelNotEqualException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleNotLoginException(NotLoginException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }

    @ExceptionHandler(BussinessException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleBusinessException(BussinessException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(
            ExceptionResponse.builder()
                            .reason(e.getMessage())
                            .build()
        );
    }
}