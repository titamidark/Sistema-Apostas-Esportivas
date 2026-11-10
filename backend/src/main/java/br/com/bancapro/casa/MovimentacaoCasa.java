package br.com.bancapro.casa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_casa")
public class MovimentacaoCasa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long casaId;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacaoCasa tipo;

    private BigDecimal valor = BigDecimal.ZERO;

    private String descricao;

    private LocalDateTime dataMovimentacao;

    @PrePersist
    public void prePersist() {
        if (dataMovimentacao == null) {
            dataMovimentacao = LocalDateTime.now();
        }

        if (valor == null) {
            valor = BigDecimal.ZERO;
        }
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

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }
}