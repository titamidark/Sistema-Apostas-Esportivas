package br.com.bancapro.application.alavancagem.dto;

import br.com.bancapro.alavancagem.ProjetoAlavancagem;
import br.com.bancapro.alavancagem.StatusProjetoAlavancagem;

import java.math.BigDecimal;

public class ProjetoAlavancagemResumoDTO {

    private Long id;
    private String nome;
    private String descricao;

    private BigDecimal valorInicial = BigDecimal.ZERO;
    private BigDecimal stakeAtual = BigDecimal.ZERO;
    private BigDecimal proximaAposta = BigDecimal.ZERO;
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
    private BigDecimal maiorStake = BigDecimal.ZERO;

    private StatusProjetoAlavancagem status;

    public static ProjetoAlavancagemResumoDTO fromEntity(
            ProjetoAlavancagem projeto
    ) {
        ProjetoAlavancagemResumoDTO dto =
                new ProjetoAlavancagemResumoDTO();

        dto.setId(projeto.getId());
        dto.setNome(projeto.getNome());
        dto.setDescricao(projeto.getDescricao());

        dto.setValorInicial(zero(projeto.getValorInicial()));
        dto.setStakeAtual(zero(projeto.getStakeAtual()));
        dto.setProximaAposta(zero(projeto.getStakeAtual()));
        dto.setLucroCicloAtual(zero(projeto.getLucroCicloAtual()));

        dto.setRodadasPorCiclo(
                projeto.getRodadasPorCiclo() == null ? 1 : projeto.getRodadasPorCiclo()
        );

        dto.setRodadaAtual(
                projeto.getRodadaAtual() == null ? 0 : projeto.getRodadaAtual()
        );

        dto.setPercentualReinvestimento(
                zero(projeto.getPercentualReinvestimento())
        );

        dto.setCicloAtual(
                projeto.getCicloAtual() == null ? 1 : projeto.getCicloAtual()
        );

        dto.setLucroRetiradoAcumulado(
                zero(projeto.getLucroRetiradoAcumulado())
        );

        dto.setRedAcumulado(
                zero(projeto.getRedAcumulado())
        );

        dto.setResultadoLiquido(
                zero(projeto.getLucroRetiradoAcumulado())
                        .subtract(zero(projeto.getRedAcumulado()))
        );

        dto.setCiclosGreen(
                projeto.getCiclosGreen() == null ? 0 : projeto.getCiclosGreen()
        );

        dto.setCiclosRed(
                projeto.getCiclosRed() == null ? 0 : projeto.getCiclosRed()
        );

        dto.setMaiorSequenciaGreen(
                projeto.getMaiorSequenciaGreen() == null ? 0 : projeto.getMaiorSequenciaGreen()
        );

        dto.setMaiorStake(zero(projeto.getMaiorStake()));
        dto.setStatus(projeto.getStatus());

        return dto;
    }

    private static BigDecimal zero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
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

    public BigDecimal getProximaAposta() { return proximaAposta; }
    public void setProximaAposta(BigDecimal proximaAposta) { this.proximaAposta = proximaAposta; }

    public BigDecimal getLucroCicloAtual() { return lucroCicloAtual; }
    public void setLucroCicloAtual(BigDecimal lucroCicloAtual) { this.lucroCicloAtual = lucroCicloAtual; }

    public Integer getRodadasPorCiclo() { return rodadasPorCiclo; }
    public void setRodadasPorCiclo(Integer rodadasPorCiclo) { this.rodadasPorCiclo = rodadasPorCiclo; }

    public Integer getRodadaAtual() { return rodadaAtual; }
    public void setRodadaAtual(Integer rodadaAtual) { this.rodadaAtual = rodadaAtual; }

    public BigDecimal getPercentualReinvestimento() { return percentualReinvestimento; }
    public void setPercentualReinvestimento(BigDecimal percentualReinvestimento) {
        this.percentualReinvestimento = percentualReinvestimento;
    }

    public Integer getCicloAtual() { return cicloAtual; }
    public void setCicloAtual(Integer cicloAtual) { this.cicloAtual = cicloAtual; }

    public BigDecimal getLucroRetiradoAcumulado() { return lucroRetiradoAcumulado; }
    public void setLucroRetiradoAcumulado(BigDecimal lucroRetiradoAcumulado) {
        this.lucroRetiradoAcumulado = lucroRetiradoAcumulado;
    }

    public BigDecimal getRedAcumulado() { return redAcumulado; }
    public void setRedAcumulado(BigDecimal redAcumulado) { this.redAcumulado = redAcumulado; }

    public BigDecimal getResultadoLiquido() { return resultadoLiquido; }
    public void setResultadoLiquido(BigDecimal resultadoLiquido) { this.resultadoLiquido = resultadoLiquido; }

    public Integer getCiclosGreen() { return ciclosGreen; }
    public void setCiclosGreen(Integer ciclosGreen) { this.ciclosGreen = ciclosGreen; }

    public Integer getCiclosRed() { return ciclosRed; }
    public void setCiclosRed(Integer ciclosRed) { this.ciclosRed = ciclosRed; }

    public Integer getMaiorSequenciaGreen() { return maiorSequenciaGreen; }
    public void setMaiorSequenciaGreen(Integer maiorSequenciaGreen) {
        this.maiorSequenciaGreen = maiorSequenciaGreen;
    }

    public BigDecimal getMaiorStake() { return maiorStake; }
    public void setMaiorStake(BigDecimal maiorStake) { this.maiorStake = maiorStake; }

    public StatusProjetoAlavancagem getStatus() { return status; }
    public void setStatus(StatusProjetoAlavancagem status) { this.status = status; }
}