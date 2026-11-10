package br.com.bancapro.cofrinho;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cofrinho_dias")
public class CofrinhoDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cofrinhoId;

    private Integer dia;

    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private StatusCofrinhoDia status = StatusCofrinhoDia.PENDENTE;

    private BigDecimal valorPrevisto = BigDecimal.ZERO;

    private BigDecimal valorDepositado = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public Long getCofrinhoId() {
        return cofrinhoId;
    }

    public void setCofrinhoId(Long cofrinhoId) {
        this.cofrinhoId = cofrinhoId;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public StatusCofrinhoDia getStatus() {
        return status;
    }

    public void setStatus(StatusCofrinhoDia status) {
        this.status = status;
    }

    public BigDecimal getValorPrevisto() {
        return valorPrevisto;
    }

    public void setValorPrevisto(BigDecimal valorPrevisto) {
        this.valorPrevisto = valorPrevisto;
    }

    public BigDecimal getValorDepositado() {
        return valorDepositado;
    }

    public void setValorDepositado(BigDecimal valorDepositado) {
        this.valorDepositado = valorDepositado;
    }
}