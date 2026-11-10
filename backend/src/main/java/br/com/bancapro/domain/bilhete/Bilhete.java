package br.com.bancapro.domain.bilhete;

import br.com.bancapro.casa.CasaAposta;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bilhetes")
public class Bilhete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private CasaAposta casa;

    @Enumerated(EnumType.STRING)
    private BilheteTipo tipo;

    @Enumerated(EnumType.STRING)
    private BilheteStatus status;

    @Enumerated(EnumType.STRING)
    private BilheteResultado resultado;

    private BigDecimal valorApostado;

    private BigDecimal oddTotal;

    private BigDecimal retornoPrevisto;

    private BigDecimal lucroPrevisto;

    private String tipster;

    private String linkTipster;

    private String observacao;

    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "bilhete",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BilheteItem> itens =
            new ArrayList<>();


    @PrePersist
    public void prePersist(){

        createdAt =
                LocalDateTime.now();

        calcularValores();

        if(resultado == null){
            resultado =
                    BilheteResultado.PENDENTE;
        }

        if(status == null){
            status =
                    BilheteStatus.AGENDADA;
        }
    }


    public void calcularValores(){

        if(
                valorApostado != null
                        &&
                        oddTotal != null
        ){

            retornoPrevisto =
                    valorApostado
                            .multiply(
                                    oddTotal
                            );

            lucroPrevisto =
                    retornoPrevisto
                            .subtract(
                                    valorApostado
                            );
        }

    }


    public Bilhete(){}


    public Long getId() {
        return id;
    }

    public CasaAposta getCasa() {
        return casa;
    }

    public void setCasa(CasaAposta casa) {
        this.casa = casa;
    }

    public BilheteTipo getTipo() {
        return tipo;
    }

    public void setTipo(BilheteTipo tipo) {
        this.tipo = tipo;
    }

    public BilheteStatus getStatus() {
        return status;
    }

    public void setStatus(BilheteStatus status) {
        this.status = status;
    }

    public BilheteResultado getResultado() {
        return resultado;
    }

    public void setResultado(
            BilheteResultado resultado
    ) {
        this.resultado = resultado;
    }

    public BigDecimal getValorApostado() {
        return valorApostado;
    }

    public void setValorApostado(
            BigDecimal valorApostado
    ) {
        this.valorApostado = valorApostado;
    }

    public BigDecimal getOddTotal() {
        return oddTotal;
    }

    public void setOddTotal(
            BigDecimal oddTotal
    ) {
        this.oddTotal = oddTotal;
    }

    public String getTipster() {
        return tipster;
    }

    public void setTipster(
            String tipster
    ) {
        this.tipster = tipster;
    }

    public String getLinkTipster() {
        return linkTipster;
    }

    public void setLinkTipster(
            String linkTipster
    ) {
        this.linkTipster = linkTipster;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(
            String observacao
    ) {
        this.observacao = observacao;
    }

    public List<BilheteItem> getItens() {
        return itens;
    }

}