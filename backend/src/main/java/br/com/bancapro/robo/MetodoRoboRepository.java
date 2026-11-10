package br.com.bancapro.robo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetodoRoboRepository extends JpaRepository<MetodoRobo, Long> {

    List<MetodoRobo> findByAtivoTrueOrderByNomeAsc();

    List<MetodoRobo> findByRoboIdAndAtivoTrueOrderByNomeAsc(Long roboId);
}