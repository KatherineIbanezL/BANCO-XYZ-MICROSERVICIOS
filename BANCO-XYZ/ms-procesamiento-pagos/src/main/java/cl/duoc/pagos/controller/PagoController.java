package cl.duoc.pagos.controller;

import cl.duoc.pagos.model.Transaccion;
import cl.duoc.pagos.service.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/retirar")
    public ResponseEntity<Transaccion> retirar(
            @RequestParam Long cuentaId, 
            @RequestParam double monto,
            @RequestParam String canal) { 
        
        // Se envían los 3 parámetros que espera el servicio
        Transaccion tx = pagoService.procesarRetiro(cuentaId, monto, canal);
        return ResponseEntity.ok(tx);
    }

    // método para usar pagoService
    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<Transaccion>> obtenerHistorial(@PathVariable Long cuentaId) {
        List<Transaccion> transacciones = pagoService.obtenerHistorialPorCuenta(cuentaId);
        return ResponseEntity.ok(transacciones);
    }
}