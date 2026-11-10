package br.com.bancapro.cofrinho;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cofrinhos")
public class Cofrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private BigDecimal valorTotal = BigDecimal.ZERO;

    private Integer dias;

    private LocalDate dataInicio;

    private LocalDate dataFinal;

    private LocalDateTime createdAt;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorReservadoInicial = BigDecimal.ZERO;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TipoCofrinho getTipo() {
        return tipo;
    }

    public void setTipo(TipoCofrinho tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private TipoCofrinho tipo = TipoCofrinho.META_FIXA;

    private BigDecimal valorOriginal = BigDecimal.ZERO;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (valorReservadoInicial == null) {
            valorReservadoInicial = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // getters e setters

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(LocalDate dataFinal) {
        this.dataFinal = dataFinal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public BigDecimal getValorReservadoInicial() {
        return valorReservadoInicial;
    }

    public void setValorReservadoInicial(BigDecimal valorReservadoInicial) {
        this.valorReservadoInicial = valorReservadoInicial;
    }

}