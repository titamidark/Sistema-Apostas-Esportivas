package br.com.bancapro.aposta.dto;

import br.com.bancapro.aposta.TipoAposta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ApostaRequestDTO {

    private Long casaId;

    private TipoAposta tipo;

    private String tipster;

    private String mercado;

    private String palpite;

    private BigDecimal valorApostado;

    private BigDecimal oddTipster;

    private BigDecimal oddFeita;

    private String linkPrint;

    private LocalDateTime dataJogo;

    private String origem = "TIPSTER";

    private Long roboId;

    private Long metodoRoboId;

    private BigDecimal retornoPrevisto;

    public Long getProjetoAlavancagemId() {
        return projetoAlavancagemId;
    }

    public void setProjetoAlavancagemId(Long projetoAlavancagemId) {
        this.projetoAlavancagemId = projetoAlavancagemId;
    }

    private Long projetoAlavancagemId;

    public Long getCasaId() {
        return casaId;
    }

    public void setCasaId(Long casaId) {
        this.casaId = casaId;
    }

    public TipoAposta getTipo() {
        return tipo;
    }

    public void setTipo(TipoAposta tipo) {
        this.tipo = tipo;
    }

    public String getMercado() {
        return mercado;
    }

    public void setMercado(String mercado) {
        this.mercado = mercado;
    }

    public String getPalpite() {
        return palpite;
    }

    public void setPalpite(String palpite) {
        this.palpite = palpite;
    }

    public BigDecimal getValorApostado() {
        return valorApostado;
    }

    public void setValorApostado(BigDecimal valorApostado) {
        this.valorApostado = valorApostado;
    }

    public BigDecimal getOddTipster() {
        return oddTipster;
    }

    public void setOddTipster(BigDecimal oddTipster) {
        this.oddTipster = oddTipster;
    }

    public BigDecimal getOddFeita() {
        return oddFeita;
    }

    public void setOddFeita(BigDecimal oddFeita) {
        this.oddFeita = oddFeita;
    }

    public String getLinkPrint() {
        return linkPrint;
    }

    public void setLinkPrint(String linkPrint) {
        this.linkPrint = linkPrint;
    }

    public LocalDateTime getDataJogo() {
        return dataJogo;
    }

    public void setDataJogo(LocalDateTime dataJogo) {
        this.dataJogo = dataJogo;
    }

    public String getTipster() {
        return tipster;
    }
    public void setTipster(String tipster) {
        this.tipster = tipster;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public Long getRoboId() {
        return roboId;
    }

    public void setRoboId(Long roboId) {
        this.roboId = roboId;
    }

    public Long getMetodoRoboId() {
        return metodoRoboId;
    }

    public void setMetodoRoboId(Long metodoRoboId) {
        this.metodoRoboId = metodoRoboId;
    }

    public BigDecimal getRetornoPrevisto() {
        return retornoPrevisto;
    }

    public void setRetornoPrevisto(BigDecimal retornoPrevisto) {
        this.retornoPrevisto = retornoPrevisto;
    }

}