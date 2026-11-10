package br.com.bancapro.adapter.in.web;

import br.com.bancapro.application.robo.RoboService;
import br.com.bancapro.application.robo.dto.MetodoRoboRequestDTO;
import br.com.bancapro.application.robo.dto.RoboRequestDTO;
import br.com.bancapro.robo.MetodoRobo;
import br.com.bancapro.robo.Robo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8100",
        "http://localhost:4200"
})
@RestController
@RequestMapping("/robos")
public class RoboController {

    private final RoboService roboService;

    public RoboController(RoboService roboService) {
        this.roboService = roboService;
    }

    @GetMapping
    public ResponseEntity<List<Robo>> listarRobos() {
        return ResponseEntity.ok(roboService.listarRobos());
    }

    @PostMapping
    public ResponseEntity<Robo> criarRobo(@RequestBody RoboRequestDTO dto) {
        return ResponseEntity.ok(roboService.criarRobo(dto));
    }

    @GetMapping("/metodos")
    public ResponseEntity<List<MetodoRobo>> listarMetodos() {
        return ResponseEntity.ok(roboService.listarMetodos());
    }

    @GetMapping("/{roboId}/metodos")
    public ResponseEntity<List<MetodoRobo>> listarMetodosPorRobo(
            @PathVariable Long roboId
    ) {
        return ResponseEntity.ok(roboService.listarMetodosPorRobo(roboId));
    }

    @PostMapping("/metodos")
    public ResponseEntity<MetodoRobo> criarMetodo(
            @RequestBody MetodoRoboRequestDTO dto
    ) {
        return ResponseEntity.ok(roboService.criarMetodo(dto));
    }
}