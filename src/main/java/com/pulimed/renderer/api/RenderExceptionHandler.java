package com.pulimed.renderer.api;

import com.pulimed.renderer.session.RenderSessionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RenderExceptionHandler {
    @ExceptionHandler(RenderSessionNotFoundException.class)
    public ResponseEntity<Map<String, String>> sessionNotFound(RenderSessionNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, String>> renderFailure(Throwable exception) {
        exception.printStackTrace();
        Throwable cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", cause.getMessage() == null ? cause.getClass().getName() : cause.getMessage()));
    }
}
