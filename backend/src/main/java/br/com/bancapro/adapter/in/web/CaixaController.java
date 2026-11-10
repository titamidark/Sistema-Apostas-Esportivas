package br.com.bancapro.adapter.in.web;

import br.com.bancapro.application.caixa.CaixaService;
import br.com.bancapro.application.caixa.dto.CaixaRequestDTO;
import br.com.bancapro.application.caixa.dto.CaixaResumoDTO;
import br.com.bancapro.caixa.MovimentacaoCaixa;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8100",
        "http://localhost:4200"
})
@RestController
@RequestMapping("/caixa")
public class CaixaController {

    private final CaixaService caixaService;

    public CaixaController(
            CaixaService caixaService
    ) {
        this.caixaService = caixaService;
    }

    @PostMapping("/movimentar")
    public ResponseEntity<MovimentacaoCaixa> movimentar(
            @RequestBody CaixaRequestDTO dto
    ) {

        return ResponseEntity.ok(

                caixaService.movimentar(
                        dto.getTipo(),
                        dto.getValor(),
                        dto.getDescricao()
                )
        );
    }

    @GetMapping("/movimentacoes")
    public ResponseEntity<List<MovimentacaoCaixa>> listar() {

        return ResponseEntity.ok(
                caixaService.listar()
        );
    }

    @GetMapping("/resumo")
    public ResponseEntity<CaixaResumoDTO> resumo() {

        return ResponseEntity.ok(
                caixaService.resumo()
        );
    }
}