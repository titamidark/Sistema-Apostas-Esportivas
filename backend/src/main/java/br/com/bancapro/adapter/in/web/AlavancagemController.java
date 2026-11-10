package br.com.bancapro.adapter.in.web;

import br.com.bancapro.alavancagem.CicloAlavancagem;
import br.com.bancapro.application.alavancagem.AlavancagemService;
import br.com.bancapro.application.alavancagem.dto.ProjetoAlavancagemRequestDTO;
import br.com.bancapro.application.alavancagem.dto.ProjetoAlavancagemResumoDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8100",
        "http://localhost:4200"
})
@RestController
@RequestMapping("/alavancagens")
public class AlavancagemController {

    private final AlavancagemService alavancagemService;

    public AlavancagemController(
            AlavancagemService alavancagemService
    ) {
        this.alavancagemService = alavancagemService;
    }

    @GetMapping
    public ResponseEntity<List<ProjetoAlavancagemResumoDTO>> listar() {
        return ResponseEntity.ok(alavancagemService.listar());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<ProjetoAlavancagemResumoDTO>> listarAtivos() {
        return ResponseEntity.ok(alavancagemService.listarAtivos());
    }

    @PostMapping
    public ResponseEntity<ProjetoAlavancagemResumoDTO> criar(
            @RequestBody ProjetoAlavancagemRequestDTO dto
    ) {
        return ResponseEntity.ok(alavancagemService.criar(dto));
    }

    @GetMapping("/{id}/proxima-aposta")
    public ResponseEntity<BigDecimal> proximaAposta(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(alavancagemService.proximaAposta(id));
    }

    @GetMapping("/{id}/ciclos")
    public ResponseEntity<List<CicloAlavancagem>> listarCiclos(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(alavancagemService.listarCiclos(id));
    }
}