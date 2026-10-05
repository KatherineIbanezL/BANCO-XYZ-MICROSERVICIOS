package cl.duoc.bff.atm.controller;

import cl.duoc.bff.atm.dto.AtmResponseDTO;
import cl.duoc.bff.atm.dto.InteresDto;
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

    @GetMapping("/intereses/{cuentaId}")
    public ResponseEntity<InteresDto> consultarInteres(@PathVariable Long cuentaId) {
        InteresDto response = atmService.obtenerInteresAtm(cuentaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/saldo/{cuentaId}")
    public ResponseEntity<AtmResponseDTO> consultarSaldo(@PathVariable String cuentaId) {
        AtmResponseDTO response = atmService.obtenerSaldoAtm(cuentaId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/retirar/{cuentaId}")
    public ResponseEntity<AtmResponseDTO> retirarEfectivo(
            @PathVariable String cuentaId, 
            @RequestParam double monto) {
        AtmResponseDTO response = atmService.retirarEfectivo(cuentaId, monto);
        return ResponseEntity.ok(response);
    }
}