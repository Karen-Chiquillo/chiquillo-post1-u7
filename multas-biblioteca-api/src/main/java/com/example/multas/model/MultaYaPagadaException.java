package com.example.multas.model;

public class MultaYaPagadaException extends RuntimeException {
    public MultaYaPagadaException(String mensaje) {
        super(mensaje);
    }
}