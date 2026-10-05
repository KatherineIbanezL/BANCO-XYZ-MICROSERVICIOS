package cl.duoc.bff.mobile.client;

import cl.duoc.bff.mobile.dto.CuentaCoreDto;
import cl.duoc.bff.mobile.dto.InteresDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
public class CoreClient {

    private final RestClient restClient;

    public CoreClient(RestClient coreRestClient) {
        this.restClient = coreRestClient;
    }

    // 1. Obtener la cuenta por ID
    @CircuitBreaker(name = "bancoCoreService", fallbackMethod = "fallbackObtenerCuenta")
    @Retry(name = "bancoCoreService")
    public CuentaCoreDto obtenerCuenta(Long id) {
        return restClient.get()
                .uri("/api/cuentas/{id}", id)
                .retrieve()
                .body(CuentaCoreDto.class);
    }

    public CuentaCoreDto fallbackObtenerCuenta(Long id, Throwable t) {
        return new CuentaCoreDto(id, id, "NO_DISPONIBLE", 0.0, "1970-01-01");
    }

    // 2. Listar todas las cuentas
    @CircuitBreaker(name = "bancoCoreService", fallbackMethod = "fallbackListarCuentas")
    @Retry(name = "bancoCoreService")
    public List<CuentaCoreDto> listarCuentas() {
        return restClient.get()
                .uri("/api/cuentas")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<CuentaCoreDto> fallbackListarCuentas(Throwable t) {
        return Collections.emptyList();
    }

    // 3. Obtener los datos de interés
    @CircuitBreaker(name = "bancoCoreService", fallbackMethod = "fallbackObtenerInteres")
    @Retry(name = "bancoCoreService")
    public InteresDto obtenerInteres(Long id) {
        return restClient.get()
                .uri("/api/intereses/{id}", id)
                .retrieve()
                .body(InteresDto.class);
    }

    public InteresDto fallbackObtenerInteres(Long id, Throwable t) {
        return new InteresDto(id, "No disponible", 0.0, 0.0, "SIN_DATOS");
    }

    // 4. Obtener el historial de movimientos
    @CircuitBreaker(name = "bancoCoreService", fallbackMethod = "fallbackObtenerHistorial")
    @Retry(name = "bancoCoreService")
    public List<CuentaCoreDto> obtenerHistorial(Long cuentaId) {
        return restClient.get()
                .uri("/api/cuentas/historial/{cuentaId}", cuentaId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<CuentaCoreDto> fallbackObtenerHistorial(Long cuentaId, Throwable t) {
        return Collections.emptyList();
    }

    // 5. Realizar un retiro de efectivo
    @CircuitBreaker(name = "bancoCoreService", fallbackMethod = "fallbackRetirarEfectivo")
    @Retry(name = "bancoCoreService")
    public CuentaCoreDto retirarEfectivo(Long id, double monto) {
        return restClient.put()
                .uri("/api/cuentas/{id}/retirar?monto={monto}", id, monto)
                .retrieve()
                .body(CuentaCoreDto.class);
    }

    public CuentaCoreDto fallbackRetirarEfectivo(Long id, double monto, Throwable t) {
        return new CuentaCoreDto(id, id, "RETIRO_FALLIDO", 0.0, "1970-01-01");
    }
}