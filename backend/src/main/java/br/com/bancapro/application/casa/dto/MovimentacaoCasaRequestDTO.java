package br.com.bancapro.application.casa.dto;

import br.com.bancapro.casa.TipoMovimentacaoCasa;
import java.math.BigDecimal;

public class MovimentacaoCasaRequestDTO {

    private TipoMovimentacaoCasa tipo;

    private BigDecimal valor;

    private String descricao;

    public TipoMovimentacaoCasa getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacaoCasa tipo) {
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