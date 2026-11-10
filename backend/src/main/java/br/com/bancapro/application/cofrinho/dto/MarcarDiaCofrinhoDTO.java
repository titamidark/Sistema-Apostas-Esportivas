package br.com.bancapro.application.cofrinho.dto;

import br.com.bancapro.cofrinho.StatusCofrinhoDia;

import java.math.BigDecimal;

public class MarcarDiaCofrinhoDTO {

    private StatusCofrinhoDia status;
    private BigDecimal valorDepositado;

    public StatusCofrinhoDia getStatus() {
        return status;
    }

    public BigDecimal getValorDepositado() {
        return valorDepositado;
    }
}