package cl.duoc.core.service;

import cl.duoc.core.model.Cuenta;
import cl.duoc.core.model.Interes;
import cl.duoc.core.repository.CuentaRepository;
import cl.duoc.core.repository.InteresRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoreService {

    private final CuentaRepository cuentaRepository;
    private final InteresRepository interesRepository;

    public CoreService(CuentaRepository cuentaRepository, InteresRepository interesRepository) {
        this.cuentaRepository = cuentaRepository;
        this.interesRepository = interesRepository;
    }

    public Cuenta obtenerCuenta(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + id));
    }

    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public Interes obtenerInteresPorId(Long id) {
        return interesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La información del Interés no ha sido encontrada para la cuenta: " + id));
    }

    public List<Cuenta> obtenerHistorialPorCuenta(Long cuentaId) {
        return cuentaRepository.findByCuentaId(cuentaId);
    }

    public List<Cuenta> obtenerTop5Movimientos(Long cuentaId) {
        return cuentaRepository.findTop5ByCuentaIdOrderByIdDesc(cuentaId);
    }

    public Cuenta retirarEfectivo(Long id, double monto) {
        Cuenta cuenta = obtenerCuenta(id);
            
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor a cero.");
        }
        
        if (cuenta.getMonto() < monto) {
            throw new RuntimeException("Saldo insuficiente para realizar el retiro.");
        }
        
        cuenta.setMonto(cuenta.getMonto() - monto);
        return cuentaRepository.save(cuenta);
    }
}