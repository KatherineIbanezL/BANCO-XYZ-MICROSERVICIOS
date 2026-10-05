package cl.duoc.bff.mobile.service;

import cl.duoc.bff.mobile.client.CoreClient;
import cl.duoc.bff.mobile.dto.CuentaCoreDto;
import cl.duoc.bff.mobile.dto.InteresDto;
import cl.duoc.bff.mobile.dto.MobileSummaryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobileService {

    private final CoreClient coreClient;

    public MobileService(CoreClient coreClient) {
        this.coreClient = coreClient;
    }

    public MobileSummaryDTO obtenerResumenMobile(String numeroCuenta) {
        Long cuentaId = Long.parseLong(numeroCuenta);
        
        InteresDto interes = coreClient.obtenerInteres(cuentaId);
        
        // método obtenerHistorial limitado a 5
        List<CuentaCoreDto> top5 = coreClient.obtenerHistorial(cuentaId).stream()
                .limit(5)
                .toList();
        
        return new MobileSummaryDTO(
            interes.cuentaId().toString(),
            interes.saldo(),
            top5
        );
    }
}