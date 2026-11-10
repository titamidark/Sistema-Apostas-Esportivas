package br.com.bancapro.domain.bilhete;

import jakarta.persistence.*;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "bilhete_itens")
public class BilheteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private String mercado;

    private BigDecimal odd;

    @ManyToOne
    @JoinColumn(name = "bilhete_id")
    @JsonIgnore
    private Bilhete bilhete;

    public BilheteItem() {
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMercado() {
        return mercado;
    }

    public void setMercado(String mercado) {
        this.mercado = mercado;
    }

    public BigDecimal getOdd() {
        return odd;
    }

    public void setOdd(BigDecimal odd) {
        this.odd = odd;
    }

    public Bilhete getBilhete() {
        return bilhete;
    }

    public void setBilhete(Bilhete bilhete) {
        this.bilhete = bilhete;
    }
}