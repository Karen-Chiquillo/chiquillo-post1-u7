package com.example.multas.model;

public class LimiteMultasPendientesException extends RuntimeException {
    public LimiteMultasPendientesException(String mensaje) {
        super(mensaje);
    }
}