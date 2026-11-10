package br.com.bancapro.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> tratarRuntime(RuntimeException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}