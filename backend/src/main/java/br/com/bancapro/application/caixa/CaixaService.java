package br.com.bancapro.application.caixa;

import br.com.bancapro.application.caixa.dto.CaixaResumoDTO;
import br.com.bancapro.caixa.MovimentacaoCaixa;
import br.com.bancapro.caixa.MovimentacaoCaixaRepository;
import br.com.bancapro.caixa.TipoMovimentacaoCaixa;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class CaixaService {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    private final MovimentacaoCaixaRepository repository;

    public CaixaService(MovimentacaoCaixaRepository repository) {
        this.repository = repository;
    }

    public MovimentacaoCaixa movimentar(
            TipoMovimentacaoCaixa tipo,
            BigDecimal valor,
            String descricao
    ) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor deve ser maior que zero");
        }

        LocalDate hoje = LocalDate.now(ZONE_ID);

        if (
                tipo == TipoMovimentacaoCaixa.SALDO_INICIAL_DIA &&
                        repository.existsByTipoAndDataReferencia(
                                TipoMovimentacaoCaixa.SALDO_INICIAL_DIA,
                                hoje
                        )
        ) {
            throw new RuntimeException("Caixa do dia já foi aberto");
        }

        MovimentacaoCaixa mov = new MovimentacaoCaixa();
        mov.setTipo(tipo);
        mov.setValor(valor);
        mov.setDescricao(descricao);
        mov.setDataReferencia(hoje);

        return repository.save(mov);
    }


    public MovimentacaoCaixa movimentarNaData(
            TipoMovimentacaoCaixa tipo,
            BigDecimal valor,
            String descricao,
            LocalDate dataReferencia
    ) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor deve ser maior que zero");
        }

        LocalDate data = dataReferencia == null
                ? LocalDate.now(ZONE_ID)
                : dataReferencia;

        if (
                tipo == TipoMovimentacaoCaixa.SALDO_INICIAL_DIA &&
                        repository.existsByTipoAndDataReferencia(
                                TipoMovimentacaoCaixa.SALDO_INICIAL_DIA,
                                data
                        )
        ) {
            throw new RuntimeException("Caixa do dia já foi aberto");
        }

        MovimentacaoCaixa mov = new MovimentacaoCaixa();
        mov.setTipo(tipo);
        mov.setValor(valor);
        mov.setDescricao(descricao);
        mov.setDataReferencia(data);

        return repository.save(mov);
    }

    public List<MovimentacaoCaixa> listar() {
        return repository.findAllByDataReferenciaOrderByDataMovimentacaoDesc(
                LocalDate.now(ZONE_ID)
        );
    }

    public CaixaResumoDTO resumo() {
        return resumoPorData(LocalDate.now(ZONE_ID));
    }

    public CaixaResumoDTO resumoPorData(LocalDate dataReferencia) {
        List<MovimentacaoCaixa> lista =
                repository.findAllByDataReferenciaOrderByDataMovimentacaoDesc(
                        dataReferencia == null ? LocalDate.now(ZONE_ID) : dataReferencia
                );

        CaixaResumoDTO dto = new CaixaResumoDTO();

        BigDecimal saldo = BigDecimal.ZERO;

        for (MovimentacaoCaixa mov : lista) {
            BigDecimal valor = mov.getValor() == null
                    ? BigDecimal.ZERO
                    : mov.getValor();

            switch (mov.getTipo()) {
                case SALDO_INICIAL_DIA -> {
                    dto.setSaldoInicial(dto.getSaldoInicial().add(valor));
                    saldo = saldo.add(valor);
                }

                case ENTRADA_MANUAL -> {
                    dto.setAdicionado(dto.getAdicionado().add(valor));
                    saldo = saldo.add(valor);
                }

                case SAIDA_MANUAL -> {
                    dto.setSaidas(dto.getSaidas().add(valor));
                    saldo = saldo.subtract(valor);
                }

                case APOSTA -> {
                    dto.setApostado(dto.getApostado().add(valor));
                    saldo = saldo.subtract(valor);
                }

                case DEPOSITO_CASA -> {
                    dto.setTransferidoParaCasas(
                            dto.getTransferidoParaCasas().add(valor)
                    );
                    saldo = saldo.subtract(valor);
                }

                case SAQUE_CASA -> {
                    dto.setSaqueDasCasas(
                            dto.getSaqueDasCasas().add(valor)
                    );
                    saldo = saldo.add(valor);
                }

                case AJUSTE -> {
                    dto.setAjustes(dto.getAjustes().add(valor));
                    saldo = saldo.add(valor);
                }
            }
        }

        dto.setSaldoAtual(saldo);

        return dto;
    }

    public BigDecimal saldoAtual() {
        CaixaResumoDTO resumo = resumo();

        if (resumo.getSaldoAtual() == null) {
            return BigDecimal.ZERO;
        }

        return resumo.getSaldoAtual();
    }

}