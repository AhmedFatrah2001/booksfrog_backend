package org.example.booksfrog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientTokensException.class)
    public ResponseEntity<Object> handleInsufficientTokensException(InsufficientTokensException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
            "status", HttpStatus.FORBIDDEN.value(),
            "error", "Insufficient Tokens",
            "message", ex.getMessage()
        ));
    }
}

