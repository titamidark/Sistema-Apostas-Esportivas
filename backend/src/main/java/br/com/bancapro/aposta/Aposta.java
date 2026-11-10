package br.com.bancapro.aposta;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "apostas")
public class Aposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long casaId;

    private String tipster;

    @Enumerated(EnumType.STRING)
    private TipoAposta tipo;

    @Enumerated(EnumType.STRING)
    private StatusAposta status;

    @Enumerated(EnumType.STRING)
    private ResultadoAposta resultado;

    private String mercado;

    private String palpite;

    private BigDecimal valorApostado = BigDecimal.ZERO;
    private BigDecimal oddTipster = BigDecimal.ZERO;
    private BigDecimal oddFeita = BigDecimal.ZERO;
    private BigDecimal retornoPrevisto = BigDecimal.ZERO;
    private BigDecimal possivelLucro = BigDecimal.ZERO;
    private BigDecimal retornoReal = BigDecimal.ZERO;
    private BigDecimal lucroPrejuizo = BigDecimal.ZERO;

    private String origem = "TIPSTER";

    private Long roboId;

    private Long metodoRoboId;

    @Column(columnDefinition = "TEXT")
    private String linkPrint;

    private LocalDateTime dataJogo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getProjetoAlavancagemId() {
        return projetoAlavancagemId;
    }

    public void setProjetoAlavancagemId(Long projetoAlavancagemId) {
        this.projetoAlavancagemId = projetoAlavancagemId;
    }

    private Long projetoAlavancagemId;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calcularPrevistos();

        if (tipster == null || tipster.isBlank()) {
            tipster = "Geral";
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        calcularPrevistos();

        if (tipster == null || tipster.isBlank()) {
            tipster = "Geral";
        }
    }

    public void calcularPrevistos() {
        BigDecimal valor = valorApostado == null ? BigDecimal.ZERO : valorApostado;
        BigDecimal odd = oddFeita == null ? BigDecimal.ZERO : oddFeita;

        if (retornoPrevisto == null || retornoPrevisto.compareTo(BigDecimal.ZERO) <= 0) {
            retornoPrevisto = valor.multiply(odd);
        }

        possivelLucro = retornoPrevisto.subtract(valor);
    }


    public Long getId() {
        return id;
    }

    public Long getCasaId() {
        return casaId;
    }

    public void setCasaId(Long casaId) {
        this.casaId = casaId;
    }

    public String getTipster() {
        return tipster;
    }

    public void setTipster(String tipster) {
        this.tipster = tipster;
    }

    public TipoAposta getTipo() {
        return tipo;
    }

    public void setTipo(TipoAposta tipo) {
        this.tipo = tipo;
    }

    public StatusAposta getStatus() {
        return status;
    }

    public void setStatus(StatusAposta status) {
        this.status = status;
    }

    public ResultadoAposta getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAposta resultado) {
        this.resultado = resultado;
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

    public BigDecimal getRetornoPrevisto() {
        return retornoPrevisto;
    }

    public void setRetornoPrevisto(BigDecimal retornoPrevisto) {
        this.retornoPrevisto = retornoPrevisto;
    }

    public BigDecimal getPossivelLucro() {
        return possivelLucro;
    }

    public void setPossivelLucro(BigDecimal possivelLucro) {
        this.possivelLucro = possivelLucro;
    }

    public BigDecimal getRetornoReal() {
        return retornoReal;
    }

    public void setRetornoReal(BigDecimal retornoReal) {
        this.retornoReal = retornoReal;
    }

    public BigDecimal getLucroPrejuizo() {
        return lucroPrejuizo;
    }

    public void setLucroPrejuizo(BigDecimal lucroPrejuizo) {
        this.lucroPrejuizo = lucroPrejuizo;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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

}