package br.com.bancapro.application.robo.dto;

public class MetodoRoboRequestDTO {

    private Long roboId;
    private String nome;
    private String descricao;
    private Boolean ativo = true;

    public Long getRoboId() {
        return roboId;
    }

    public void setRoboId(Long roboId) {
        this.roboId = roboId;
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


    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}