package br.com.bancapro.application.dia.dto;

import br.com.bancapro.dia.DiaOperacional;
import br.com.bancapro.dia.StatusDiaOperacional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DiaOperacionalDTO {
    private Long id;
    private LocalDate data;
    private StatusDiaOperacional status;
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

    public static DiaOperacionalDTO fromEntity(DiaOperacional dia) {
        DiaOperacionalDTO dto = new DiaOperacionalDTO();
        dto.setId(dia.getId());
        dto.setData(dia.getData());
        dto.setStatus(dia.getStatus());
        dto.setSaldoInicial(dia.getSaldoInicial());
        dto.setSaldoFinal(dia.getSaldoFinal());
        dto.setTotalApostado(dia.getTotalApostado());
        dto.setTotalGreen(dia.getTotalGreen());
        dto.setTotalRed(dia.getTotalRed());
        dto.setTotalCashout(dia.getTotalCashout());
        dto.setLucroPrejuizo(dia.getLucroPrejuizo());
        dto.setRoi(dia.getRoi());
        dto.setQuantidadeApostas(dia.getQuantidadeApostas());
        dto.setQuantidadePendentes(dia.getQuantidadePendentes());
        dto.setAbertoEm(dia.getAbertoEm());
        dto.setFechadoEm(dia.getFechadoEm());
        return dto;
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
