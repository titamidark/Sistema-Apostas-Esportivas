package br.com.bancapro.dia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaOperacionalRepository extends JpaRepository<DiaOperacional, Long> {

    Optional<DiaOperacional> findByData(LocalDate data);

    Optional<DiaOperacional> findFirstByStatusOrderByDataDesc(StatusDiaOperacional status);

    Optional<DiaOperacional> findFirstByStatusOrderByDataAsc(StatusDiaOperacional status);

    Optional<DiaOperacional> findFirstByStatusOrderByDataDescAbertoEmDesc(StatusDiaOperacional status);

    Optional<DiaOperacional> findFirstByStatusOrderByDataDescIdDesc(StatusDiaOperacional status);

    Optional<DiaOperacional> findFirstByStatusOrderByDataDescFechadoEmDesc(StatusDiaOperacional status);

    List<DiaOperacional> findTop30ByOrderByDataDesc();
}
