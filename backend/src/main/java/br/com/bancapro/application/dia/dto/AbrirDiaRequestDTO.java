package br.com.bancapro.application.dia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AbrirDiaRequestDTO {
    private LocalDate data;
    private BigDecimal saldoInicial;

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }
}
