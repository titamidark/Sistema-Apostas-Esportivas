package br.com.bancapro.adapter.in.web;

import br.com.bancapro.application.dashboard.DashboardService;
import br.com.bancapro.application.dashboard.dto.DashboardResumoDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {
        "http://localhost:8100",
        "http://localhost:4200"
})
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<DashboardResumoDTO> resumo() {
        return ResponseEntity.ok(
                dashboardService.resumo()
        );
    }

    @PostMapping("/zerar-tudo")
    public ResponseEntity<Void> zerarTudo() {

        dashboardService.zerarTudo();

        return ResponseEntity.ok().build();
    }


}