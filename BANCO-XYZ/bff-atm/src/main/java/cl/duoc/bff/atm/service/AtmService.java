package cl.duoc.bff.atm.service;

import cl.duoc.bff.atm.client.CoreClient;
import cl.duoc.bff.atm.dto.AtmResponseDTO;
import cl.duoc.bff.atm.dto.InteresDto;
import org.springframework.stereotype.Service;

@Service
public class AtmService {

    private final CoreClient coreClient;

    public AtmService(CoreClient coreClient) {
        this.coreClient = coreClient;
    }

    public AtmResponseDTO obtenerSaldoAtm(String numeroCuenta) {
        Long cuentaId = Long.parseLong(numeroCuenta);
        InteresDto interes = coreClient.obtenerInteres(cuentaId);
        
        return new AtmResponseDTO(
            interes.cuentaId().toString(),
            interes.saldo(),
            "CONSULTA_EXITOSA"
        );
    }

    public AtmResponseDTO retirarEfectivo(String numeroCuenta, double monto) {
        Long cuentaId = Long.parseLong(numeroCuenta);
        // Invoca al Core para realizar el descuento y persistencia
        coreClient.retirarEfectivo(cuentaId, monto);
        
        InteresDto interes = coreClient.obtenerInteres(cuentaId);
        return new AtmResponseDTO(
            interes.cuentaId().toString(),
            interes.saldo(),
            "RETIRO_EXITOSO"
        );
    }

    public InteresDto obtenerInteresAtm(Long cuentaId) {
        return coreClient.obtenerInteres(cuentaId);
    }   
}