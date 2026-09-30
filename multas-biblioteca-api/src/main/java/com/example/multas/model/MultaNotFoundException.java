package com.example.multas.model;

public class MultaNotFoundException extends RuntimeException {
    public MultaNotFoundException(String mensaje) {
        super(mensaje);
    }
}