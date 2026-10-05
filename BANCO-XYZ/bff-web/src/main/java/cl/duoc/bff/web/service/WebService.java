package cl.duoc.bff.web.service;

import cl.duoc.bff.web.client.CoreClient;
import cl.duoc.bff.web.dto.CuentaCoreDto;
import cl.duoc.bff.web.dto.InteresDto;
import cl.duoc.bff.web.dto.WebDashboardDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebService {

    private final CoreClient coreClient;

    public WebService(CoreClient coreClient) {
        this.coreClient = coreClient;
    }

    public WebDashboardDTO obtenerDashboardWeb(String numeroCuenta) {
        Long cuentaId = Long.parseLong(numeroCuenta);
        
        InteresDto interes = coreClient.obtenerInteres(cuentaId);
        List<CuentaCoreDto> historial = coreClient.obtenerHistorial(cuentaId);
        
        return new WebDashboardDTO(
            interes.cuentaId().toString(),
            interes.nombre(),
            interes.saldo(),
            historial
        );
    }
}