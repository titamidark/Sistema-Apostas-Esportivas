package br.com.bancapro.robo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoboRepository extends JpaRepository<Robo, Long> {

    List<Robo> findByAtivoTrueOrderByNomeAsc();
}