package br.com.bancapro.application.caixa.dto;

import java.math.BigDecimal;

public class CaixaResumoDTO {

    private BigDecimal saldoInicial = BigDecimal.ZERO;
    private BigDecimal adicionado = BigDecimal.ZERO;
    private BigDecimal saidas = BigDecimal.ZERO;
    private BigDecimal apostado = BigDecimal.ZERO;
    private BigDecimal transferidoParaCasas = BigDecimal.ZERO;
    private BigDecimal saqueDasCasas = BigDecimal.ZERO;
    private BigDecimal ajustes = BigDecimal.ZERO;
    private BigDecimal saldoAtual = BigDecimal.ZERO;

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getAdicionado() {
        return adicionado;
    }

    public void setAdicionado(BigDecimal adicionado) {
        this.adicionado = adicionado;
    }

    public BigDecimal getSaidas() {
        return saidas;
    }

    public void setSaidas(BigDecimal saidas) {
        this.saidas = saidas;
    }

    public BigDecimal getApostado() {
        return apostado;
    }

    public void setApostado(BigDecimal apostado) {
        this.apostado = apostado;
    }

    public BigDecimal getTransferidoParaCasas() {
        return transferidoParaCasas;
    }

    public void setTransferidoParaCasas(BigDecimal transferidoParaCasas) {
        this.transferidoParaCasas = transferidoParaCasas;
    }

    public BigDecimal getSaqueDasCasas() {
        return saqueDasCasas;
    }

    public void setSaqueDasCasas(BigDecimal saqueDasCasas) {
        this.saqueDasCasas = saqueDasCasas;
    }

    public BigDecimal getAjustes() {
        return ajustes;
    }

    public void setAjustes(BigDecimal ajustes) {
        this.ajustes = ajustes;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }
}