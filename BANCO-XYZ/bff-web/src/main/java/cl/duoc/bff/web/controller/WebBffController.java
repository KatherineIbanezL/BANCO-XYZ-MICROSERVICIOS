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

    @GetMapping("/dashboard/{cuentaId}")
    public ResponseEntity<WebDashboardDTO> obtenerDashboard(@PathVariable String cuentaId) {
        WebDashboardDTO dashboard = webService.obtenerDashboardWeb(cuentaId);
        return ResponseEntity.ok(dashboard);
    }
}