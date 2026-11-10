package br.com.bancapro.alavancagem;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ciclos_alavancagem")
public class CicloAlavancagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projetoId;
    private Integer numero;

    private BigDecimal valorInicial = BigDecimal.ZERO;
    private BigDecimal stakeInicial = BigDecimal.ZERO;
    private BigDecimal stakeFinal = BigDecimal.ZERO;
    private BigDecimal lucroCiclo = BigDecimal.ZERO;
    private BigDecimal valorRetirado = BigDecimal.ZERO;

    private Integer rodadasPlanejadas = 1;
    private Integer rodadasRealizadas = 0;

    @Enumerated(EnumType.STRING)
    private StatusCicloAlavancagem status = StatusCicloAlavancagem.ABERTO;

    private LocalDateTime criadoEm;
    private LocalDateTime fechadoEm;

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public BigDecimal getValorInicial() { return valorInicial; }
    public void setValorInicial(BigDecimal valorInicial) { this.valorInicial = valorInicial; }

    public BigDecimal getStakeInicial() { return stakeInicial; }
    public void setStakeInicial(BigDecimal stakeInicial) { this.stakeInicial = stakeInicial; }

    public BigDecimal getStakeFinal() { return stakeFinal; }
    public void setStakeFinal(BigDecimal stakeFinal) { this.stakeFinal = stakeFinal; }

    public BigDecimal getLucroCiclo() { return lucroCiclo; }
    public void setLucroCiclo(BigDecimal lucroCiclo) { this.lucroCiclo = lucroCiclo; }

    public BigDecimal getValorRetirado() { return valorRetirado; }
    public void setValorRetirado(BigDecimal valorRetirado) { this.valorRetirado = valorRetirado; }

    public Integer getRodadasPlanejadas() { return rodadasPlanejadas; }
    public void setRodadasPlanejadas(Integer rodadasPlanejadas) { this.rodadasPlanejadas = rodadasPlanejadas; }

    public Integer getRodadasRealizadas() { return rodadasRealizadas; }
    public void setRodadasRealizadas(Integer rodadasRealizadas) { this.rodadasRealizadas = rodadasRealizadas; }

    public StatusCicloAlavancagem getStatus() { return status; }
    public void setStatus(StatusCicloAlavancagem status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getFechadoEm() { return fechadoEm; }
    public void setFechadoEm(LocalDateTime fechadoEm) { this.fechadoEm = fechadoEm; }
}