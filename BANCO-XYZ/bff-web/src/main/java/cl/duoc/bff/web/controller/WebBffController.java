package cl.duoc.bff.web.controller;

import cl.duoc.bff.web.dto.WebDashboardDTO;
import cl.duoc.bff.web.service.WebService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/web")
public class WebBffController {

    private final WebService webService;

    public WebBffController(WebService webService) {
        this.webService = webService;
    }

    @GetMapping("/dashboard/{clienteId}")
    public ResponseEntity<WebDashboardDTO> obtenerDashboard(
            @PathVariable Long clienteId,
            @RequestHeader("Authorization") String token) {
        
        WebDashboardDTO dashboard = webService.obtenerDashboardCompleto(clienteId, token);
        return ResponseEntity.ok(dashboard);
    }
}