package cl.duoc.bff.mobile.service;

import cl.duoc.bff.mobile.dto.CuentaDTO;
import cl.duoc.bff.mobile.dto.MobileSummaryDTO;
import cl.duoc.bff.mobile.dto.TransaccionDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class MobileService {

    private final RestClient restClient;

    public MobileService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @CircuitBreaker(name = "backend-cb", fallbackMethod = "resumenFallback")
    public MobileSummaryDTO obtenerResumenMobile(Long clienteId, String token) {
        // 1. Obtiene las cuentas del cliente desde ms-gestion-cuentas
        List<CuentaDTO> cuentas = restClient.get()
                .uri("http://ms-gestion-cuentas/api/cuentas/cliente/{id}", clienteId)
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CuentaDTO>>() {});

        if (cuentas == null || cuentas.isEmpty()) {
            return new MobileSummaryDTO("SIN CUENTA", 0.0, new ArrayList<>());
        }

        // Tomamos la cuenta principal del cliente
        CuentaDTO cuentaPrincipal = cuentas.get(0);

        // 2. Obtiene el historial de movimientos para esa cuenta desde ms-procesamiento-pagos
        List<TransaccionDTO> historialCompleto = restClient.get()
                .uri("http://ms-procesamiento-pagos/api/pagos/cuenta/{id}", cuentaPrincipal.getId())
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<TransaccionDTO>>() {});

        // 3. Aplica la lógica móvil: Limitar a los últimos 5 movimientos
        List<TransaccionDTO> top5 = (historialCompleto != null) 
                ? historialCompleto.stream().limit(5).toList() 
                : new ArrayList<>();

        // 4. Retorna el DTO móvil (mapeando cuentaPrincipal.getMonto() como saldo)
        return new MobileSummaryDTO(
                cuentaPrincipal.getNumeroCuenta(),
                cuentaPrincipal.getMonto(),
                top5
        );
    }

    public MobileSummaryDTO resumenFallback(Long clienteId, String token, Throwable throwable) {
        throwable.printStackTrace();
        return new MobileSummaryDTO(
                "NO DISPONIBLE", 
                0.0, 
                new ArrayList<>()
        );
    }
}