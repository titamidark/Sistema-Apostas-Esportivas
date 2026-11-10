package br.com.bancapro.adapter.in.web;

import br.com.bancapro.application.dia.DiaOperacionalService;
import br.com.bancapro.application.dia.dto.AbrirDiaRequestDTO;
import br.com.bancapro.application.dia.dto.DiaOperacionalDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8100",
        "http://localhost:4200"
})
@RestController
@RequestMapping("/dia-operacional")
public class DiaOperacionalController {

    private final DiaOperacionalService service;

    public DiaOperacionalController(DiaOperacionalService service) {
        this.service = service;
    }

    @GetMapping("/atual")
    public ResponseEntity<DiaOperacionalDTO> atual() {
        return ResponseEntity.ok(service.resumoDiaAtual());
    }

    @PostMapping("/abrir")
    public ResponseEntity<DiaOperacionalDTO> abrir(@RequestBody AbrirDiaRequestDTO dto) {
        return ResponseEntity.ok(service.abrirDia(dto.getData(), dto.getSaldoInicial()));
    }

    @PostMapping("/fechar")
    public ResponseEntity<DiaOperacionalDTO> fechar() {
        return ResponseEntity.ok(service.fecharDiaAtual());
    }

    @PostMapping("/iniciar-proximo")
    public ResponseEntity<DiaOperacionalDTO> iniciarProximo() {
        return ResponseEntity.ok(service.iniciarProximoDia());
    }

    @GetMapping("/historico")
    public ResponseEntity<List<DiaOperacionalDTO>> historico() {
        return ResponseEntity.ok(service.historico());
    }
}
