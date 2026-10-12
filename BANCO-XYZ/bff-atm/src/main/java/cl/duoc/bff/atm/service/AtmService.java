package cl.duoc.bff.atm.service;

import cl.duoc.bff.atm.dto.AtmResponseDTO;
import cl.duoc.bff.atm.dto.CuentaDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AtmService {

    private final RestClient restClient;

    public AtmService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    // --- RETIROS ---
    @CircuitBreaker(name = "backend-cb", fallbackMethod = "retiroFallback")
    public AtmResponseDTO procesarRetiro(Long cuentaId, double monto, String token) {
        // Ejecuta el POST sin asignar a una variable
        restClient.post()
                .uri("http://ms-procesamiento-pagos/api/pagos/retirar?cuentaId={c}&monto={m}&canal=ATM", cuentaId, monto)
                .header("Authorization", token)
                .retrieve()
                .toBodilessEntity(); 
        
        return new AtmResponseDTO("Retiro procesado exitosamente", monto);
    }

    // Fallback para Retiros (Retorna AtmResponseDTO)
    public AtmResponseDTO retiroFallback(Long cuentaId, double monto, String token, Throwable throwable) {
        // El cajero mostrará este mensaje en pantalla en vez de arrojar error 500
        return new AtmResponseDTO("Error: Servicio de retiros no disponible. Intente más tarde.", 0.0);
    }

    // --- CONSULTA DE SALDO ---
    @CircuitBreaker(name = "backend-cb", fallbackMethod = "saldoFallback")
    public CuentaDTO consultarSaldo(Long cuentaId, String token) {
        // Llama directamente a ms-gestion-cuentas para obtener el saldo
        return restClient.get()
                .uri("http://ms-gestion-cuentas/api/cuentas/{id}", cuentaId)
                .header("Authorization", token)
                .retrieve()
                .body(CuentaDTO.class);
    }

    // Fallback para Saldo (Retorna CuentaDTO)
    public CuentaDTO saldoFallback(Long cuentaId, String token, Throwable throwable) {
        CuentaDTO cuentaInactiva = new CuentaDTO();
        cuentaInactiva.setNumeroCuenta("NO DISPONIBLE");
        cuentaInactiva.setMonto(0.0);
        return cuentaInactiva;
    }
}