package cl.duoc.bff.mobile.controller;

import cl.duoc.bff.mobile.dto.MobileSummaryDTO;
import cl.duoc.bff.mobile.service.MobileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile")
public class MobileBffController {

    private final MobileService mobileService;

    public MobileBffController(MobileService mobileService) {
        this.mobileService = mobileService;
    }

    @GetMapping("/resumen/{cuentaId}")
    public ResponseEntity<MobileSummaryDTO> obtenerResumen(@PathVariable Long cuentaId, @RequestHeader("Authorization") String token) {
        MobileSummaryDTO resumen = mobileService.obtenerResumenMobile(cuentaId, token);
        return ResponseEntity.ok(resumen);
    }
}