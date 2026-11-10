package br.com.bancapro.cofrinho;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CofrinhoDiaRepository extends JpaRepository<CofrinhoDia, Long> {

    List<CofrinhoDia> findByCofrinhoIdOrderByDiaAsc(Long cofrinhoId);

    @Modifying
    @Query("delete from CofrinhoDia d where d.cofrinhoId = :cofrinhoId")
    void deletarPorCofrinhoId(
            @Param("cofrinhoId") Long cofrinhoId
    );

}