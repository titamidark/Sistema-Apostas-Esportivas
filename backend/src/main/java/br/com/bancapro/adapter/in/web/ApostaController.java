package br.com.bancapro.adapter.in.web;

import br.com.bancapro.aposta.Aposta;
import br.com.bancapro.aposta.dto.ApostaRequestDTO;
import br.com.bancapro.application.aposta.ApostaService;
import br.com.bancapro.application.aposta.dto.FecharApostaRequestDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8100",
        "http://localhost:4200"
})
@RestController
@RequestMapping("/apostas")
public class ApostaController {

    private final ApostaService apostaService;

    public ApostaController(ApostaService apostaService) {
        this.apostaService = apostaService;
    }

    @GetMapping
    public ResponseEntity<List<Aposta>> listar() {
        return ResponseEntity.ok(apostaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aposta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(apostaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Aposta> criar(@RequestBody ApostaRequestDTO dto) {
        return ResponseEntity.ok(apostaService.criar(dto));
    }

    @PostMapping("/{id}/fazer")
    public ResponseEntity<Aposta> fazerAposta(@PathVariable Long id) {
        return ResponseEntity.ok(apostaService.fazerAposta(id));
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<Aposta> fechar(
            @PathVariable Long id,
            @RequestBody FecharApostaRequestDTO dto
    ) {
        return ResponseEntity.ok(apostaService.fechar(id, dto));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Aposta> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(apostaService.cancelar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aposta> atualizar(
            @PathVariable Long id,
            @RequestBody ApostaRequestDTO dto
    ) {
        return ResponseEntity.ok(apostaService.atualizar(id, dto));
    }

    @PostMapping("/{id}/reabrir")
    public ResponseEntity<Aposta> reabrir(@PathVariable Long id) {
        return ResponseEntity.ok(apostaService.reabrir(id));
    }

}