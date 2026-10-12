package cl.duoc.bff.atm.controller;

import cl.duoc.bff.atm.dto.AtmResponseDTO;
import cl.duoc.bff.atm.dto.CuentaDTO;
import cl.duoc.bff.atm.service.AtmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/atm")
public class AtmBffController {

    private final AtmService atmService;

    public AtmBffController(AtmService atmService) {
        this.atmService = atmService;
    }

    @PostMapping("/retiro")
    public ResponseEntity<AtmResponseDTO> procesarRetiro(
            @RequestParam Long cuentaId, 
            @RequestParam double monto, 
            @RequestHeader("Authorization") String token) {
        
        AtmResponseDTO response = atmService.procesarRetiro(cuentaId, monto, token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/saldo/{cuentaId}")
    public ResponseEntity<CuentaDTO> consultarSaldo(
            @PathVariable Long cuentaId,
            @RequestHeader("Authorization") String token) {
        
        CuentaDTO cuenta = atmService.consultarSaldo(cuentaId, token);
        return ResponseEntity.ok(cuenta);
    }
}