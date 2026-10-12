package cl.duoc.cuentas.controller;

import cl.duoc.cuentas.model.Cuenta;
import cl.duoc.cuentas.model.Interes;
import cl.duoc.cuentas.service.CuentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> obtenerCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.obtenerCuenta(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Cuenta>> obtenerCuentasDeCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(cuentaService.obtenerCuentasPorCliente(clienteId));
    }

    @GetMapping("/{id}/intereses")
    public ResponseEntity<List<Interes>> obtenerIntereses(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.obtenerInteresesPorCuenta(id));
    }

    // Endpoint crítico: Usado por ms-procesamiento-pagos vía OpenFeign para afectar el saldo
    @PutMapping("/{id}/saldo")
    public ResponseEntity<Cuenta> modificarSaldo(@PathVariable Long id, @RequestBody Map<String, Double> payload) {
        Double montoVariacion = payload.get("monto");
        if (montoVariacion == null) {
            return ResponseEntity.badRequest().build();
        }
        Cuenta cuentaActualizada = cuentaService.actualizarSaldo(id, montoVariacion);
        return ResponseEntity.ok(cuentaActualizada);
    }
}