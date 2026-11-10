package br.com.bancapro.application.caixa.dto;

import br.com.bancapro.caixa.TipoMovimentacaoCaixa;

import java.math.BigDecimal;

public class CaixaRequestDTO {

    private TipoMovimentacaoCaixa tipo;

    private BigDecimal valor;

    private String descricao;

    public TipoMovimentacaoCaixa getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacaoCaixa tipo) {
        this.tipo = tipo;
    }

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