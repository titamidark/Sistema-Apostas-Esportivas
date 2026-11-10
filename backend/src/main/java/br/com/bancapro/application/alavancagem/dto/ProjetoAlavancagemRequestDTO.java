package br.com.bancapro.application.alavancagem.dto;

import java.math.BigDecimal;

public class ProjetoAlavancagemRequestDTO {

    private String nome;
    private String descricao;

    private BigDecimal valorInicial;
    private Integer rodadasPorCiclo;
    private BigDecimal percentualReinvestimento;

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


    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }


    public Integer getRodadasPorCiclo() {
        return rodadasPorCiclo;
    }

    public void setRodadasPorCiclo(Integer rodadasPorCiclo) {
        this.rodadasPorCiclo = rodadasPorCiclo;
    }


    public BigDecimal getPercentualReinvestimento() {
        return percentualReinvestimento;
    }

    public void setPercentualReinvestimento(BigDecimal percentualReinvestimento) {
        this.percentualReinvestimento = percentualReinvestimento;
    }
}