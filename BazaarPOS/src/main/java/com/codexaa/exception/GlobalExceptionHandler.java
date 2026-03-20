package com.codexaa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));
    }

    @ExceptionHandler(UserExceptions.NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(UserExceptions.NotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserExceptions.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(UserExceptions.AccessDeniedException ex) {
        return error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(UserExceptions.InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(UserExceptions.InsufficientStockException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UserExceptions.InvalidOrderStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatus(UserExceptions.InvalidOrderStatusException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserExceptions.DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(UserExceptions.DuplicateResourceException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UserExceptions.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(UserExceptions ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    }
}