package br.com.bancapro.caixa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovimentacaoCaixaRepository
        extends JpaRepository<MovimentacaoCaixa, Long> {

    List<MovimentacaoCaixa>
    findAllByDataReferenciaOrderByDataMovimentacaoDesc(
            LocalDate dataReferencia
    );

    boolean existsByTipoAndDataReferencia(
            TipoMovimentacaoCaixa tipo,
            LocalDate dataReferencia
    );
}