package br.com.bancapro.alavancagem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjetoAlavancagemRepository
        extends JpaRepository<ProjetoAlavancagem, Long> {

    List<ProjetoAlavancagem> findByStatusOrderByIdDesc(
            StatusProjetoAlavancagem status
    );

    List<ProjetoAlavancagem> findAllByOrderByIdDesc();
}