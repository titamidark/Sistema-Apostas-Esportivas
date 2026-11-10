package br.com.bancapro.application.bilhete.dto;

import br.com.bancapro.application.bilhete.dto.CriarBilheteItemRequest;
import br.com.bancapro.domain.bilhete.BilheteTipo;

import java.math.BigDecimal;
import java.util.List;

public class CriarBilheteRequest {

    private Long casaId;

    private BilheteTipo tipo;

    private BigDecimal valorApostado;

    private String tipster;

    private String linkTipster;

    private String observacao;

    private List<CriarBilheteItemRequest> itens;


    public Long getCasaId() {
        return casaId;
    }

    public void setCasaId(
            Long casaId
    ) {
        this.casaId = casaId;
    }

    public BilheteTipo getTipo() {
        return tipo;
    }

    public void setTipo(
            BilheteTipo tipo
    ) {
        this.tipo = tipo;
    }

    public BigDecimal getValorApostado() {
        return valorApostado;
    }

    public void setValorApostado(
            BigDecimal valorApostado
    ) {
        this.valorApostado = valorApostado;
    }

    public String getTipster() {
        return tipster;
    }

    public void setTipster(
            String tipster
    ) {
        this.tipster = tipster;
    }

    public String getLinkTipster() {
        return linkTipster;
    }

    public void setLinkTipster(
            String linkTipster
    ) {
        this.linkTipster = linkTipster;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(
            String observacao
    ) {
        this.observacao = observacao;
    }

    public List<CriarBilheteItemRequest> getItens() {
        return itens;
    }

    public void setItens(
            List<CriarBilheteItemRequest> itens
    ) {
        this.itens = itens;
    }
}