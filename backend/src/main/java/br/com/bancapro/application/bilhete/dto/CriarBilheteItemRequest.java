package br.com.bancapro.application.bilhete.dto;

import java.math.BigDecimal;

public class CriarBilheteItemRequest {

    private String descricao;

    private String mercado;

    private BigDecimal odd;


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(
            String descricao
    ) {
        this.descricao = descricao;
    }

    public String getMercado() {
        return mercado;
    }

    public void setMercado(
            String mercado
    ) {
        this.mercado = mercado;
    }

    public BigDecimal getOdd() {
        return odd;
    }

    public void setOdd(
            BigDecimal odd
    ) {
        this.odd = odd;
    }
}