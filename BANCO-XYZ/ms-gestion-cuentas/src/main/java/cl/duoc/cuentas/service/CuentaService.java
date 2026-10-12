package cl.duoc.cuentas.service;

import cl.duoc.cuentas.model.Cuenta;
import cl.duoc.cuentas.model.Interes;
import cl.duoc.cuentas.repository.CuentaRepository;
import cl.duoc.cuentas.repository.InteresRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaService {

    private static final Logger log = LoggerFactory.getLogger(CuentaService.class);

    private final CuentaRepository cuentaRepository;
    private final InteresRepository interesRepository;

    public CuentaService(CuentaRepository cuentaRepository, InteresRepository interesRepository) {
        this.cuentaRepository = cuentaRepository;
        this.interesRepository = interesRepository;
    }

    public Cuenta obtenerCuenta(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + id));
    }

    public List<Cuenta> obtenerCuentasPorCliente(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId);
    }

    public List<Interes> obtenerInteresesPorCuenta(Long cuentaId) {
        return interesRepository.findByCuentaId(cuentaId);
    }

    @Transactional
    public Cuenta actualizarSaldo(Long cuentaId, double montoVariacion) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuentaId);
        Cuenta cuenta;

        if (cuentaOpt.isPresent()) {
            cuenta = cuentaOpt.get();
            double saldoActual = cuenta.getMonto() != null ? cuenta.getMonto() : 0.0;
            double nuevoSaldo = saldoActual + montoVariacion;
            
            // Advertencia si hay sobregiro, pero permitimos que la BD se sincronice
            // con el microservicio de Pagos para mantener consistencia.
            if (nuevoSaldo < 0) {
                log.warn("Alerta de Sobregiro: La cuenta {} ha quedado con saldo negativo ({}).", cuentaId, nuevoSaldo);
            }
            
            cuenta.setMonto(nuevoSaldo);
            log.info("Saldo actualizado para cuenta ID {}. Nuevo saldo: {}", cuentaId, nuevoSaldo);
        } else {
            // Autoprovisionamiento: Si Kafka envía un pago de una cuenta que el Batch aún no carga
            log.warn("La cuenta ID {} no existía en BD. Se autoprovisionará.", cuentaId);
            cuenta = new Cuenta();
            cuenta.setId(cuentaId);
            cuenta.setNumeroCuenta("CTA-" + cuentaId);
            cuenta.setClienteId(cuentaId);
            cuenta.setMonto(Math.max(0.0, montoVariacion));
            cuenta.setTipoCuenta("AHORRO");
        }

        return cuentaRepository.save(cuenta);
    }
}