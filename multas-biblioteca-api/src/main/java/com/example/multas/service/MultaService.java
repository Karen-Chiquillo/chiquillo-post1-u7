package com.example.multas.service;

import com.example.multas.domain.PagoRechazadoException;
import com.example.multas.domain.ResultadoPago;
import com.example.multas.domain.port.PasarelaPagoPort;
import com.example.multas.model.EstadoMulta;
import com.example.multas.model.LimiteMultasPendientesException;
import com.example.multas.model.Multa;
import com.example.multas.model.MultaNotFoundException;
import com.example.multas.model.MultaYaPagadaException;
import com.example.multas.repository.MultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MultaService {

    private static final int LIMITE_MULTAS_PENDIENTES = 3;
    private final MultaRepository multaRepository;
    private final PasarelaPagoPort pasarelaPagoPort; 

    public MultaService(MultaRepository multaRepository, PasarelaPagoPort pasarelaPagoPort) {
        this.multaRepository = multaRepository;
        this.pasarelaPagoPort = pasarelaPagoPort;
    }

    public List<Multa> listarTodas() {
        return multaRepository.findAll();
    }

    public List<Multa> listarPorEstudiante(String estudianteId) {
        return multaRepository.findByEstudianteId(estudianteId);
    }

    public Multa buscarPorId(Long id) {
        return multaRepository.findById(id)
                .orElseThrow(() -> new MultaNotFoundException("Multa " + id + " no encontrada"));
    }

    public Multa generar(String estudianteId, String concepto, int diasAtraso) {
        // Regla 1 (requiere BD): Validar límite de multas pendientes (Punto de decisión 2)
        Long pendientes = multaRepository.countByEstudianteIdAndEstado(estudianteId, EstadoMulta.PENDIENTE);
        if (pendientes >= LIMITE_MULTAS_PENDIENTES) {
            throw new LimiteMultasPendientesException(
                    "El estudiante " + estudianteId + " ya tiene " + pendientes + 
                    " multas pendientes (limite: " + LIMITE_MULTAS_PENDIENTES + ")"
            );
        }

        Multa multa = new Multa();
        multa.setEstudianteId(estudianteId);
        multa.setConcepto(concepto);
        multa.setDiasAtraso(diasAtraso);
        multa.setMonto(Multa.calcularMonto(diasAtraso)); // Regla 2: delegada a la entidad (Punto de decisión 1)

        return multaRepository.save(multa);
    }

    public Multa pagarEnVentanilla(Long id) {
        Multa multa = buscarPorId(id);
        multa.marcarComoPagada("VENTANILLA");
        return multaRepository.save(multa);
    }

    
    public Multa pagarConPasarela(Long id) {
        Multa multa = buscarPorId(id);
        if (multa.getEstado() == EstadoMulta.PAGADA) {
            throw new MultaYaPagadaException("La multa " + id + " ya fue pagada el " + multa.getFechaPago());
        }

        ResultadoPago resultado = pasarelaPagoPort.procesar(multa);

        if (!resultado.exitoso()) {
            throw new PagoRechazadoException(resultado.mensaje());
        }

        multa.marcarComoPagada(resultado.proveedor());
        return multaRepository.save(multa);
    }
}