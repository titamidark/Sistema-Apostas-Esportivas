package br.com.bancapro.aposta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ApostaRepository extends JpaRepository<Aposta, Long> {

    List<Aposta> findAllByOrderByCreatedAtDesc();

    List<Aposta> findByStatusOrderByCreatedAtDesc(StatusAposta status);

    List<Aposta> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime inicio, LocalDateTime fim);
}