package br.com.bancapro.application.casa.dto;

import java.math.BigDecimal;

public class AtualizarSaldoCasaRequestDTO {

    private BigDecimal novoSaldo;
    private BigDecimal retiradaPessoal;

    public BigDecimal getNovoSaldo() {
        return novoSaldo;
    }

    public void setNovoSaldo(BigDecimal novoSaldo) {
        this.novoSaldo = novoSaldo;
    }

    public BigDecimal getRetiradaPessoal() {
        return retiradaPessoal;
    }

    public void setRetiradaPessoal(BigDecimal retiradaPessoal) {
        this.retiradaPessoal = retiradaPessoal;
    }
}