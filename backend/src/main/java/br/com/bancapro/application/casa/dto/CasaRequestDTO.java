package br.com.bancapro.application.casa.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class CasaRequestDTO {

    @NotBlank
    private String nome;

    private BigDecimal saldoInicial = BigDecimal.ZERO;

    private Boolean ativa = true;

    private String linkSite;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public String getLinkSite() {
        return linkSite;
    }

    public void setLinkSite(String linkSite) {
        this.linkSite = linkSite;
    }

}

