package cl.duoc.bff.web.service;

import cl.duoc.bff.web.dto.ClienteDTO;
import cl.duoc.bff.web.dto.CuentaDTO;
import cl.duoc.bff.web.dto.TransaccionDTO;
import cl.duoc.bff.web.dto.WebDashboardDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebService {

    private final RestClient restClient;

    public WebService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }
    
    // 1. Se enlaza al cortacircuitos y se define qué método ejecutar si falla
    @CircuitBreaker(name = "backend-cb", fallbackMethod = "dashboardFallback")
    public WebDashboardDTO obtenerDashboardCompleto(Long clienteId, String token) {
        // 1. Obtiene perfil completo desde ms-gestion-clientes
        ClienteDTO cliente = restClient.get()
                .uri("http://ms-gestion-clientes/api/clientes/{id}", clienteId)
                .header("Authorization", token)
                .retrieve()
                .body(ClienteDTO.class);

        // 2. Obtiene las cuentas base y saldos desde ms-gestion-cuentas
        List<CuentaDTO> cuentas = restClient.get()
                .uri("http://ms-gestion-cuentas/api/cuentas/cliente/{id}", clienteId)
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CuentaDTO>>() {});

        // 3. Obtiene el historial COMPLETO para cada cuenta desde ms-procesamiento-pagos
        if (cuentas != null) {
            for (CuentaDTO cuenta : cuentas) {
                List<TransaccionDTO> historialCompleto = restClient.get()
                        .uri("http://ms-procesamiento-pagos/api/pagos/cuenta/{id}", cuenta.getId())
                        .header("Authorization", token)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<TransaccionDTO>>() {});
                
                // Se inyecta el historial completo a la cuenta antes de enviarlo al frontend
                cuenta.setTransacciones(historialCompleto); 
            }
        }

        // 4. Retorna el objeto consolidado con toda la información
        return new WebDashboardDTO(cliente, cuentas);
    }

   // 2. Método Fallback para la Web (comportamiento alternativo)
    public WebDashboardDTO dashboardFallback(Long clienteId, String token, Throwable throwable) {
        // Crea un cliente "falso" para avisar en pantalla que hay un problema
        ClienteDTO clienteFalla = new ClienteDTO();
        clienteFalla.setNombre("Servicio Temporalmente No Disponible");
        throwable.printStackTrace();
        
        // Retorna el DTO correcto: El cliente con el aviso, y una lista de cuentas vacía
        return new WebDashboardDTO(clienteFalla, new java.util.ArrayList<>());
    }
}