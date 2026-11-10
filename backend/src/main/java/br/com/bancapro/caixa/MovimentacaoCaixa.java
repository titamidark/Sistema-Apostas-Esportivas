package br.com.bancapro.caixa;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_caixa")
public class MovimentacaoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacaoCaixa tipo;

    private BigDecimal valor = BigDecimal.ZERO;

    private String descricao;

    private LocalDateTime dataMovimentacao;

    @PrePersist
    public void prePersist() {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }

        if (dataMovimentacao == null) {
            dataMovimentacao = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

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

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public void setId(Long id) {
        this.id = id;
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

    private LocalDate dataReferencia;

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }
}