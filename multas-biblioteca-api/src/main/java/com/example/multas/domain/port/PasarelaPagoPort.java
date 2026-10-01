package com.example.multas.domain.port;

import com.example.multas.domain.ResultadoPago;
import com.example.multas.model.Multa;

public interface PasarelaPagoPort {
    ResultadoPago procesar(Multa multa);
}