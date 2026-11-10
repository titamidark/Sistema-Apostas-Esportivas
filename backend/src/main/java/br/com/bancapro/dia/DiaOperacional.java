package br.com.bancapro.dia;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dias_operacionais")
public class DiaOperacional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDiaOperacional status = StatusDiaOperacional.ABERTO;

    private BigDecimal saldoInicial = BigDecimal.ZERO;
    private BigDecimal saldoFinal = BigDecimal.ZERO;
    private BigDecimal totalApostado = BigDecimal.ZERO;
    private BigDecimal totalGreen = BigDecimal.ZERO;
    private BigDecimal totalRed = BigDecimal.ZERO;
    private BigDecimal totalCashout = BigDecimal.ZERO;
    private BigDecimal lucroPrejuizo = BigDecimal.ZERO;
    private BigDecimal roi = BigDecimal.ZERO;

    private Long quantidadeApostas = 0L;
    private Long quantidadePendentes = 0L;

    private LocalDateTime abertoEm;
    private LocalDateTime fechadoEm;

    @PrePersist
    public void prePersist() {
        if (abertoEm == null) {
            abertoEm = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusDiaOperacional.ABERTO;
        }
        normalizarValores();
    }

    @PreUpdate
    public void preUpdate() {
        normalizarValores();
    }

    private void normalizarValores() {
        if (saldoInicial == null) saldoInicial = BigDecimal.ZERO;
        if (saldoFinal == null) saldoFinal = BigDecimal.ZERO;
        if (totalApostado == null) totalApostado = BigDecimal.ZERO;
        if (totalGreen == null) totalGreen = BigDecimal.ZERO;
        if (totalRed == null) totalRed = BigDecimal.ZERO;
        if (totalCashout == null) totalCashout = BigDecimal.ZERO;
        if (lucroPrejuizo == null) lucroPrejuizo = BigDecimal.ZERO;
        if (roi == null) roi = BigDecimal.ZERO;
        if (quantidadeApostas == null) quantidadeApostas = 0L;
        if (quantidadePendentes == null) quantidadePendentes = 0L;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public StatusDiaOperacional getStatus() { return status; }
    public void setStatus(StatusDiaOperacional status) { this.status = status; }
    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }
    public BigDecimal getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(BigDecimal saldoFinal) { this.saldoFinal = saldoFinal; }
    public BigDecimal getTotalApostado() { return totalApostado; }
    public void setTotalApostado(BigDecimal totalApostado) { this.totalApostado = totalApostado; }
    public BigDecimal getTotalGreen() { return totalGreen; }
    public void setTotalGreen(BigDecimal totalGreen) { this.totalGreen = totalGreen; }
    public BigDecimal getTotalRed() { return totalRed; }
    public void setTotalRed(BigDecimal totalRed) { this.totalRed = totalRed; }
    public BigDecimal getTotalCashout() { return totalCashout; }
    public void setTotalCashout(BigDecimal totalCashout) { this.totalCashout = totalCashout; }
    public BigDecimal getLucroPrejuizo() { return lucroPrejuizo; }
    public void setLucroPrejuizo(BigDecimal lucroPrejuizo) { this.lucroPrejuizo = lucroPrejuizo; }
    public BigDecimal getRoi() { return roi; }
    public void setRoi(BigDecimal roi) { this.roi = roi; }
    public Long getQuantidadeApostas() { return quantidadeApostas; }
    public void setQuantidadeApostas(Long quantidadeApostas) { this.quantidadeApostas = quantidadeApostas; }
    public Long getQuantidadePendentes() { return quantidadePendentes; }
    public void setQuantidadePendentes(Long quantidadePendentes) { this.quantidadePendentes = quantidadePendentes; }
    public LocalDateTime getAbertoEm() { return abertoEm; }
    public void setAbertoEm(LocalDateTime abertoEm) { this.abertoEm = abertoEm; }
    public LocalDateTime getFechadoEm() { return fechadoEm; }
    public void setFechadoEm(LocalDateTime fechadoEm) { this.fechadoEm = fechadoEm; }
}
