package br.com.bancapro.adapter.in.web;

import br.com.bancapro.aposta.Aposta;
import br.com.bancapro.aposta.ApostaRepository;
import br.com.bancapro.aposta.StatusAposta;
import br.com.bancapro.application.casa.CasaService;
import br.com.bancapro.application.casa.dto.AtualizarSaldoCasaRequestDTO;
import br.com.bancapro.application.casa.dto.CasaRequestDTO;
import br.com.bancapro.application.casa.dto.MovimentacaoCasaRequestDTO;
import br.com.bancapro.casa.CasaAposta;
import br.com.bancapro.casa.MovimentacaoCasa;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8100",
        "http://localhost:4200"
})
@RestController
@RequestMapping("/casas")
public class CasaController {

    private final CasaService casaService;
    private final ApostaRepository apostaRepository;

    public CasaController(
            CasaService casaService,
            ApostaRepository apostaRepository
    ) {
        this.casaService = casaService;
        this.apostaRepository = apostaRepository;
    }

    @PostMapping
    public ResponseEntity<CasaAposta> criar(
            @RequestBody @Valid CasaRequestDTO dto
    ) {
        CasaAposta casa = new CasaAposta();

        casa.setNome(dto.getNome());
        casa.setSaldoInicial(dto.getSaldoInicial());
        casa.setAtiva(dto.getAtiva());
        casa.setLinkSite(dto.getLinkSite());

        return ResponseEntity.ok(casaService.criar(casa));
    }

    @GetMapping
    public ResponseEntity<List<CasaAposta>> listar() {
        return ResponseEntity.ok(casaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CasaAposta> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(casaService.buscarPorId(id));
    }


    @PostMapping("/{id}/atualizar-saldo")
    public ResponseEntity<CasaAposta> atualizarSaldo(
            @PathVariable Long id,
            @RequestBody AtualizarSaldoCasaRequestDTO dto
    ) {
        return ResponseEntity.ok(
                casaService.atualizarSaldoAtual(
                        id,
                        dto.getNovoSaldo(),
                        dto.getRetiradaPessoal()
                )
        );
    }

    @GetMapping("/{id}/resultado-atualizacao-saldo-hoje")
    public ResponseEntity<BigDecimal> resultadoAtualizacaoSaldoHoje(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                casaService.resultadoAtualizacaoSaldoHoje(id)
        );
    }

    @GetMapping("/{id}/valor-apostado-aberto")
    public ResponseEntity<BigDecimal> valorApostadoAberto(
            @PathVariable Long id
    ) {
        BigDecimal total = apostaRepository.findAll()
                .stream()
                .filter(aposta -> aposta.getCasaId() != null)
                .filter(aposta -> aposta.getCasaId().equals(id))
                .filter(aposta ->
                        aposta.getStatus() == StatusAposta.APOSTA_FEITA ||
                                aposta.getStatus() == StatusAposta.EM_ANDAMENTO
                )
                .map(Aposta::getValorApostado)
                .map(valor -> valor == null ? BigDecimal.ZERO : valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(total);
    }

    @GetMapping("/{id}/valor-agendado")
    public ResponseEntity<BigDecimal> valorAgendado(
            @PathVariable Long id
    ) {
        BigDecimal total = apostaRepository.findAll()
                .stream()
                .filter(aposta -> aposta.getCasaId() != null)
                .filter(aposta -> aposta.getCasaId().equals(id))
                .filter(aposta -> aposta.getStatus() == StatusAposta.AGENDADA)
                .map(Aposta::getValorApostado)
                .map(valor -> valor == null ? BigDecimal.ZERO : valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(total);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CasaAposta> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CasaRequestDTO dto
    ) {
        CasaAposta casa = new CasaAposta();

        casa.setNome(dto.getNome());
        casa.setSaldoInicial(dto.getSaldoInicial());
        casa.setAtiva(dto.getAtiva());
        casa.setLinkSite(dto.getLinkSite());

        return ResponseEntity.ok(casaService.atualizar(id, casa));
    }

    @PostMapping("/{id}/movimentacoes")
    public ResponseEntity<MovimentacaoCasa> movimentar(
            @PathVariable Long id,
            @RequestBody MovimentacaoCasaRequestDTO dto
    ) {
        return ResponseEntity.ok(
                casaService.movimentar(
                        id,
                        dto.getTipo(),
                        dto.getValor(),
                        dto.getDescricao()
                )
        );
    }

    @GetMapping("/{id}/movimentacoes")
    public ResponseEntity<List<MovimentacaoCasa>> listarMovimentacoes(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(casaService.listarMovimentacoes(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id
    ) {
        casaService.deletar(id);

        return ResponseEntity.noContent().build();
    }
}