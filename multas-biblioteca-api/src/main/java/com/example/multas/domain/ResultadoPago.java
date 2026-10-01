package com.example.multas.domain;

public record ResultadoPago(
    String proveedor,
    boolean exitoso,
    String referenciaExterna,
    String mensaje
) {}