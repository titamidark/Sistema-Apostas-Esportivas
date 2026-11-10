package br.com.bancapro.adapter.in.web;

import br.com.bancapro.application.bilhete.BilheteService;
import br.com.bancapro.application.bilhete.dto.CriarBilheteRequest;
import br.com.bancapro.domain.bilhete.Bilhete;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bilhetes")
public class BilheteController {

    private final BilheteService bilheteService;

    public BilheteController(BilheteService bilheteService) {
        this.bilheteService = bilheteService;
    }

    @PostMapping
    public ResponseEntity<Bilhete> criar(
            @RequestBody CriarBilheteRequest request
    ) {
        return ResponseEntity.ok(
                bilheteService.criar(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<Bilhete>> listar() {
        return ResponseEntity.ok(
                bilheteService.listar()
        );
    }
}