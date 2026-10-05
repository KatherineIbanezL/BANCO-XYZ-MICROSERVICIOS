package cl.duoc.bff.mobile.client;

import cl.duoc.bff.mobile.dto.CuentaCoreDto;
import cl.duoc.bff.mobile.dto.InteresDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CoreClient {

    private final RestClient restClient;

    // Spring inyectará automáticamente el RestClient configurado con la URL del Core (puerto 8080)
    public CoreClient(RestClient coreRestClient) {
        this.restClient = coreRestClient;
    }

    // 1. Obtener la cuenta por ID
    public CuentaCoreDto obtenerCuenta(Long id) {
        return restClient.get()
                .uri("/api/cuentas/{id}", id)
                .retrieve()
                .body(CuentaCoreDto.class);
    }

    // 2. Listar todas las cuentas
    public List<CuentaCoreDto> listarCuentas() {
        return restClient.get()
                .uri("/api/cuentas")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    // 3. Obtener los datos de interés (saldo, etc.)
    public InteresDto obtenerInteres(Long id) {
        return restClient.get()
                .uri("/api/intereses/{id}", id)
                .retrieve()
                .body(InteresDto.class);
    }

    // 4. Obtener el historial de movimientos de una cuenta
    public List<CuentaCoreDto> obtenerHistorial(Long cuentaId) {
        return restClient.get()
                .uri("/api/cuentas/historial/{cuentaId}", cuentaId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    // 5. Realizar un retiro de efectivo en el Core
    public CuentaCoreDto retirarEfectivo(Long id, double monto) {
        return restClient.put()
                .uri("/api/cuentas/{id}/retirar?monto={monto}", id, monto)
                .retrieve()
                .body(CuentaCoreDto.class);
    }
}