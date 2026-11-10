package br.com.bancapro.adapter.out.persistence;

import br.com.bancapro.domain.bilhete.Bilhete;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BilheteRepository extends JpaRepository<Bilhete, Long> {
}