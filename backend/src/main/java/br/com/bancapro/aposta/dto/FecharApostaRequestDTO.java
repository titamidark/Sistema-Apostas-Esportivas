package br.com.bancapro.application.aposta.dto;

import br.com.bancapro.aposta.ResultadoAposta;

import java.math.BigDecimal;

public class FecharApostaRequestDTO {

    private ResultadoAposta resultado;

    private BigDecimal retornoReal;

    public ResultadoAposta getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAposta resultado) {
        this.resultado = resultado;
    }

    public BigDecimal getRetornoReal() {
        return retornoReal;
    }

    public void setRetornoReal(BigDecimal retornoReal) {
        this.retornoReal = retornoReal;
    }
}