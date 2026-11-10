package br.com.bancapro.application.cofrinho.dto;

import br.com.bancapro.cofrinho.TipoCofrinho;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CofrinhoRequestDTO {

    private String nome;
    private String descricao;
    private BigDecimal valorTotal;
    private LocalDate dataInicio;
    private Integer dias;

    private BigDecimal valorReservadoInicial;

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public TipoCofrinho getTipo() {
        return tipo;
    }

    public void setTipo(TipoCofrinho tipo) {
        this.tipo = tipo;
    }

    private TipoCofrinho tipo;

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public Integer getDias() {
        return dias;
    }

    public BigDecimal getValorReservadoInicial() {
        return valorReservadoInicial;
    }

    public void setValorReservadoInicial(BigDecimal valorReservadoInicial) {
        this.valorReservadoInicial = valorReservadoInicial;
    }
}