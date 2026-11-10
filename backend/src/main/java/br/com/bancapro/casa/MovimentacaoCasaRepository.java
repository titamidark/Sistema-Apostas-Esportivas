package br.com.bancapro.casa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoCasaRepository extends JpaRepository<MovimentacaoCasa, Long> {

    List<MovimentacaoCasa> findByCasaIdOrderByDataMovimentacaoDesc(Long casaId);

    boolean existsByCasaIdAndTipoNot(Long casaId, TipoMovimentacaoCasa tipo);

    List<MovimentacaoCasa> findByDataMovimentacaoBetweenOrderByDataMovimentacaoDesc(LocalDateTime inicio, LocalDateTime fim);
}