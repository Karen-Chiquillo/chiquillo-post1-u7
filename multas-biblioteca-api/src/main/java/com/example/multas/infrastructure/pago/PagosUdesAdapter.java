package com.example.multas.infrastructure.pago;

import com.example.multas.domain.ResultadoPago;
import com.example.multas.domain.port.PasarelaPagoPort;
import com.example.multas.model.Multa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@ConditionalOnProperty(prefix = "app.pagos", name = "proveedor", havingValue = "pagosudes", matchIfMissing = true)
public class PagosUdesAdapter implements PasarelaPagoPort {

    private final RestTemplate restTemplate;

    @Value("${app.pagos.pagosudes.url:http://localhost:9001/pagosudes/transacciones}")
    private String urlPasarela;

    public PagosUdesAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResultadoPago procesar(Multa multa) {
        try {
            PagosUdesRequest request = new PagosUdesRequest(multa.getEstudianteId(), multa.getMonto());
            PagosUdesResponse response = restTemplate.postForObject(urlPasarela, request, PagosUdesResponse.class);

            boolean exitoso = response != null && "APROBADA".equals(response.estadoTransaccion());

            return new ResultadoPago(
                "PAGOSUDES",
                exitoso,
                response != null ? response.idTransaccion() : null,
                exitoso ? "Pago aprobado por PagosUDES" : "Transaccion rechazada por PagosUDES"
            );
        } catch (RestClientException ex) {
            return new ResultadoPago("PAGOSUDES", false, null, "PagosUDES no disponible: " + ex.getMessage());
        }
    }

    private record PagosUdesRequest(String estudianteId, BigDecimal monto) {}
    private record PagosUdesResponse(String idTransaccion, String estadoTransaccion) {}
}