package com.example.multas.controller;

import com.example.multas.domain.PagoRechazadoException;
import com.example.multas.model.LimiteMultasPendientesException;
import com.example.multas.model.MultaNotFoundException;
import com.example.multas.model.MultaYaPagadaException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(MultaNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler({LimiteMultasPendientesException.class, MultaYaPagadaException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflicto(RuntimeException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return errores;
    }

    
    @ExceptionHandler(PagoRechazadoException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public Map<String, String> handlePagoRechazado(PagoRechazadoException ex) {
        return Map.of("error", ex.getMessage());
    }
}