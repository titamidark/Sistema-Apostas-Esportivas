package br.com.bancapro.application.cofrinho.dto;

import java.math.BigDecimal;

public class AdicionarGastoCofrinhoDTO {

    private BigDecimal valor;
    private String descricao;

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}