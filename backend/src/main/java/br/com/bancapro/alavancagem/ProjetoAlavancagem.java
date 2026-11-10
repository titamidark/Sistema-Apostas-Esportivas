package br.com.bancapro.alavancagem;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "projetos_alavancagem")
public class ProjetoAlavancagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;

    private BigDecimal valorInicial = BigDecimal.ZERO;
    private BigDecimal stakeAtual = BigDecimal.ZERO;
    private BigDecimal lucroCicloAtual = BigDecimal.ZERO;

    private Integer rodadasPorCiclo = 1;
    private Integer rodadaAtual = 0;
    private BigDecimal percentualReinvestimento = BigDecimal.valueOf(100);

    private Integer cicloAtual = 1;

    private BigDecimal lucroRetiradoAcumulado = BigDecimal.ZERO;
    private BigDecimal redAcumulado = BigDecimal.ZERO;
    private BigDecimal resultadoLiquido = BigDecimal.ZERO;

    private Integer ciclosGreen = 0;
    private Integer ciclosRed = 0;

    private Integer maiorSequenciaGreen = 0;
    private Integer sequenciaAtualGreen = 0;

    private BigDecimal maiorStake = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private StatusProjetoAlavancagem status = StatusProjetoAlavancagem.ATIVO;

    private LocalDateTime criadoEm;
    private LocalDateTime encerradoEm;

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();

        if (stakeAtual == null || stakeAtual.compareTo(BigDecimal.ZERO) == 0) {
            stakeAtual = valorInicial;
        }

        if (maiorStake == null || maiorStake.compareTo(BigDecimal.ZERO) == 0) {
            maiorStake = stakeAtual;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (lucroRetiradoAcumulado == null) lucroRetiradoAcumulado = BigDecimal.ZERO;
        if (redAcumulado == null) redAcumulado = BigDecimal.ZERO;

        resultadoLiquido = lucroRetiradoAcumulado.subtract(redAcumulado);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValorInicial() { return valorInicial; }
    public void setValorInicial(BigDecimal valorInicial) { this.valorInicial = valorInicial; }

    public BigDecimal getStakeAtual() { return stakeAtual; }
    public void setStakeAtual(BigDecimal stakeAtual) { this.stakeAtual = stakeAtual; }

    public BigDecimal getLucroCicloAtual() { return lucroCicloAtual; }
    public void setLucroCicloAtual(BigDecimal lucroCicloAtual) { this.lucroCicloAtual = lucroCicloAtual; }

    public Integer getRodadasPorCiclo() { return rodadasPorCiclo; }
    public void setRodadasPorCiclo(Integer rodadasPorCiclo) { this.rodadasPorCiclo = rodadasPorCiclo; }

    public Integer getRodadaAtual() { return rodadaAtual; }
    public void setRodadaAtual(Integer rodadaAtual) { this.rodadaAtual = rodadaAtual; }

    public BigDecimal getPercentualReinvestimento() { return percentualReinvestimento; }
    public void setPercentualReinvestimento(BigDecimal percentualReinvestimento) { this.percentualReinvestimento = percentualReinvestimento; }

    public Integer getCicloAtual() { return cicloAtual; }
    public void setCicloAtual(Integer cicloAtual) { this.cicloAtual = cicloAtual; }

    public BigDecimal getLucroRetiradoAcumulado() { return lucroRetiradoAcumulado; }
    public void setLucroRetiradoAcumulado(BigDecimal lucroRetiradoAcumulado) { this.lucroRetiradoAcumulado = lucroRetiradoAcumulado; }

    public BigDecimal getRedAcumulado() { return redAcumulado; }
    public void setRedAcumulado(BigDecimal redAcumulado) { this.redAcumulado = redAcumulado; }

    public BigDecimal getResultadoLiquido() { return resultadoLiquido; }
    public void setResultadoLiquido(BigDecimal resultadoLiquido) { this.resultadoLiquido = resultadoLiquido; }

    public Integer getCiclosGreen() { return ciclosGreen; }
    public void setCiclosGreen(Integer ciclosGreen) { this.ciclosGreen = ciclosGreen; }

    public Integer getCiclosRed() { return ciclosRed; }
    public void setCiclosRed(Integer ciclosRed) { this.ciclosRed = ciclosRed; }

    public Integer getMaiorSequenciaGreen() { return maiorSequenciaGreen; }
    public void setMaiorSequenciaGreen(Integer maiorSequenciaGreen) { this.maiorSequenciaGreen = maiorSequenciaGreen; }

    public Integer getSequenciaAtualGreen() { return sequenciaAtualGreen; }
    public void setSequenciaAtualGreen(Integer sequenciaAtualGreen) { this.sequenciaAtualGreen = sequenciaAtualGreen; }

    public BigDecimal getMaiorStake() { return maiorStake; }
    public void setMaiorStake(BigDecimal maiorStake) { this.maiorStake = maiorStake; }

    public StatusProjetoAlavancagem getStatus() { return status; }
    public void setStatus(StatusProjetoAlavancagem status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getEncerradoEm() { return encerradoEm; }
    public void setEncerradoEm(LocalDateTime encerradoEm) { this.encerradoEm = encerradoEm; }
}