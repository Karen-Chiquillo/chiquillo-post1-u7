package com.example.multas.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GenerarMultaRequest(
    @NotBlank(message = "El código de estudiante es obligatorio")
    String estudianteId,

    @NotBlank(message = "El concepto es obligatorio")
    String concepto,

    @Min(value = 1, message = "Los dias de atraso deben ser al menos 1")
    int diasAtraso
) {}