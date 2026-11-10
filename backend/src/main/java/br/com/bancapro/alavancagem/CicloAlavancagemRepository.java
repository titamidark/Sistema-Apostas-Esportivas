package br.com.bancapro.alavancagem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CicloAlavancagemRepository
        extends JpaRepository<CicloAlavancagem, Long> {

    List<CicloAlavancagem> findByProjetoIdOrderByNumeroDesc(Long projetoId);

    Optional<CicloAlavancagem> findFirstByProjetoIdAndStatusOrderByNumeroDesc(
            Long projetoId,
            StatusCicloAlavancagem status
    );
}