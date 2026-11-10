package br.com.bancapro.adapter.in.web;

import br.com.bancapro.application.tipster.TipsterService;
import br.com.bancapro.application.tipster.dto.TipsterRequestDTO;
import br.com.bancapro.tipster.Tipster;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8100",
        "http://localhost:4200"
})
@RestController
@RequestMapping("/tipsters")
public class TipsterController {

    private final TipsterService tipsterService;

    public TipsterController(TipsterService tipsterService) {
        this.tipsterService = tipsterService;
    }

    @GetMapping
    public ResponseEntity<List<Tipster>> listar() {
        return ResponseEntity.ok(tipsterService.listar());
    }

    @PostMapping
    public ResponseEntity<Tipster> criar(@RequestBody TipsterRequestDTO dto) {
        return ResponseEntity.ok(tipsterService.criar(dto));
    }
}