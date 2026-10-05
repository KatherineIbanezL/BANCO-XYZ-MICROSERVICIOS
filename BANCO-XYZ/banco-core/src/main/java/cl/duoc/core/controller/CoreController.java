package cl.duoc.core.controller;

import cl.duoc.core.model.Cuenta;
import cl.duoc.core.model.Interes;
import cl.duoc.core.service.CoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CoreController {

    private final CoreService coreService;

    public CoreController(CoreService coreService) {
        this.coreService = coreService;
    }

    @GetMapping("/cuentas/{id}")
    public ResponseEntity<Cuenta> obtenerCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(coreService.obtenerCuenta(id));
    }

    @GetMapping("/cuentas")
    public ResponseEntity<List<Cuenta>> listarCuentas() {
        return ResponseEntity.ok(coreService.listarCuentas());
    }

    @GetMapping("/intereses/{id}")
    public ResponseEntity<Interes> obtenerInteres(@PathVariable Long id) {
        return ResponseEntity.ok(coreService.obtenerInteresPorId(id));
    }

    @GetMapping("/cuentas/historial/{cuentaId}")
    public ResponseEntity<List<Cuenta>> obtenerHistorial(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(coreService.obtenerHistorialPorCuenta(cuentaId));
    }

    @PutMapping("/cuentas/{id}/retirar")
    public ResponseEntity<Cuenta> retirarEfectivo(@PathVariable Long id, @RequestParam double monto) {
        return ResponseEntity.ok(coreService.retirarEfectivo(id, monto));
    }
}