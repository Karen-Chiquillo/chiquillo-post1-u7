package com.example.multas.controller;

import com.example.multas.model.Multa;
import com.example.multas.service.MultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/multas")
public class MultaController {

    private final MultaService multaService;

    public MultaController(MultaService multaService) {
        this.multaService = multaService;
    }

    @GetMapping
    public List<Multa> listar() {
        return multaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Multa buscar(@PathVariable Long id) {
        return multaService.buscarPorId(id);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public List<Multa> listarPorEstudiante(@PathVariable String estudianteId) {
        return multaService.listarPorEstudiante(estudianteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Multa generar(@Valid @RequestBody GenerarMultaRequest request) {
        return multaService.generar(request.estudianteId(), request.concepto(), request.diasAtraso());
    }

    @PatchMapping("/{id}/pagar")
    public Multa pagarEnVentanilla(@PathVariable Long id) {
        return multaService.pagarEnVentanilla(id);
    }

    
    @PostMapping("/{id}/pagar-en-linea")
    public Multa pagarEnLinea(@PathVariable Long id) {
        return multaService.pagarConPasarela(id);
    }
}