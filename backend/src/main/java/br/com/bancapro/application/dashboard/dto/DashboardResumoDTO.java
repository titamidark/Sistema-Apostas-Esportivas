package br.com.bancapro.application.dashboard.dto;

import br.com.bancapro.application.dia.dto.DiaOperacionalDTO;

import java.math.BigDecimal;

public class DashboardResumoDTO {

    private BigDecimal caixaAtual = BigDecimal.ZERO;
    private BigDecimal saldoTotalCasas = BigDecimal.ZERO;
    private BigDecimal patrimonioTotal = BigDecimal.ZERO;
    private BigDecimal totalApostado = BigDecimal.ZERO;
    private BigDecimal lucroPrejuizo = BigDecimal.ZERO;
    private Long quantidadeJogosAgendados = 0L;
    private BigDecimal valorTotalJogosAgendados = BigDecimal.ZERO;
    private DiaOperacionalDTO diaOperacional;

    private Integer apostasEmAberto = 0;

    public Integer getApostasEmAberto() {
        return apostasEmAberto;
    }

    public void setApostasEmAberto(Integer apostasEmAberto) {
        this.apostasEmAberto = apostasEmAberto;
    }

    private BigDecimal recuperacaoPotencial = BigDecimal.ZERO;
    private BigDecimal projecaoDia = BigDecimal.ZERO;

    public DiaOperacionalDTO getDiaOperacional() {
        return diaOperacional;
    }

    public void setDiaOperacional(DiaOperacionalDTO diaOperacional) {
        this.diaOperacional = diaOperacional;
    }

    public BigDecimal getCaixaAtual() {
        return caixaAtual;
    }

    public void setCaixaAtual(BigDecimal caixaAtual) {
        this.caixaAtual = caixaAtual;
    }

    public BigDecimal getSaldoTotalCasas() {
        return saldoTotalCasas;
    }

    public void setSaldoTotalCasas(BigDecimal saldoTotalCasas) {
        this.saldoTotalCasas = saldoTotalCasas;
    }

    public BigDecimal getPatrimonioTotal() {
        return patrimonioTotal;
    }

    public void setPatrimonioTotal(BigDecimal patrimonioTotal) {
        this.patrimonioTotal = patrimonioTotal;
    }

    public BigDecimal getTotalApostado() {
        return totalApostado;
    }

    public void setTotalApostado(BigDecimal totalApostado) {
        this.totalApostado = totalApostado;
    }

    public BigDecimal getLucroPrejuizo() {
        return lucroPrejuizo;
    }

    public BigDecimal getRecuperacaoPotencial() {
        return recuperacaoPotencial;
    }

    public void setRecuperacaoPotencial(BigDecimal recuperacaoPotencial) {
        this.recuperacaoPotencial = recuperacaoPotencial;
    }

    public BigDecimal getProjecaoDia() {
        return projecaoDia;
    }

    public void setProjecaoDia(BigDecimal projecaoDia) {
        this.projecaoDia = projecaoDia;
    }

    public void setLucroPrejuizo(BigDecimal lucroPrejuizo) {
        this.lucroPrejuizo = lucroPrejuizo;
    }

    public Long getQuantidadeJogosAgendados() {
        return quantidadeJogosAgendados;
    }

    public void setQuantidadeJogosAgendados(Long quantidadeJogosAgendados) {
        this.quantidadeJogosAgendados = quantidadeJogosAgendados;
    }

    public BigDecimal getValorTotalJogosAgendados() {
        return valorTotalJogosAgendados;
    }

    public void setValorTotalJogosAgendados(BigDecimal valorTotalJogosAgendados) {
        this.valorTotalJogosAgendados = valorTotalJogosAgendados;
    }
}