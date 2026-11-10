package br.com.bancapro.adapter.in.web;

import br.com.bancapro.application.cofrinho.CofrinhoService;
import br.com.bancapro.application.cofrinho.dto.AdicionarGastoCofrinhoDTO;
import br.com.bancapro.application.cofrinho.dto.CofrinhoRequestDTO;
import br.com.bancapro.application.cofrinho.dto.MarcarDiaCofrinhoDTO;
import br.com.bancapro.cofrinho.Cofrinho;
import br.com.bancapro.cofrinho.CofrinhoDia;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8100",
        "http://localhost:4200"
})
@RestController
@RequestMapping("/cofrinhos")
public class CofrinhoController {

    private final CofrinhoService cofrinhoService;

    public CofrinhoController(CofrinhoService cofrinhoService) {
        this.cofrinhoService = cofrinhoService;
    }

    @GetMapping
    public ResponseEntity<List<Cofrinho>> listar() {
        return ResponseEntity.ok(cofrinhoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cofrinho> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cofrinhoService.buscarPorId(id));
    }

    @GetMapping("/{id}/dias")
    public ResponseEntity<List<CofrinhoDia>> listarDias(@PathVariable Long id) {
        return ResponseEntity.ok(cofrinhoService.listarDias(id));
    }

    @PostMapping
    public ResponseEntity<Cofrinho> criar(@RequestBody CofrinhoRequestDTO dto) {
        return ResponseEntity.ok(cofrinhoService.criar(dto));
    }

    @PostMapping("/{cofrinhoId}/dias/{diaId}/marcar")
    public ResponseEntity<CofrinhoDia> marcarDia(
            @PathVariable Long cofrinhoId,
            @PathVariable Long diaId,
            @RequestBody MarcarDiaCofrinhoDTO dto
    ) {
        return ResponseEntity.ok(
                cofrinhoService.marcarDia(cofrinhoId, diaId, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cofrinhoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/dias-restantes")
    public ResponseEntity<Void> alterarDiasRestantes(
            @PathVariable Long id,
            @RequestParam Integer diasRestantes
    ) {
        cofrinhoService.alterarDiasRestantes(id, diasRestantes);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{cofrinhoId}/gastos")
    public ResponseEntity<Cofrinho> adicionarGasto(
            @PathVariable Long cofrinhoId,
            @RequestBody AdicionarGastoCofrinhoDTO dto
    ) {
        return ResponseEntity.ok(
                cofrinhoService.adicionarGasto(cofrinhoId, dto)
        );
    }

}