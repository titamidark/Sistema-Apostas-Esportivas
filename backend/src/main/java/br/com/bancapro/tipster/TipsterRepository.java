package br.com.bancapro.tipster;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipsterRepository extends JpaRepository<Tipster, Long> {

    List<Tipster> findByAtivoTrueOrderByNomeAsc();

    List<Tipster> findAllByOrderByNomeAsc();
}