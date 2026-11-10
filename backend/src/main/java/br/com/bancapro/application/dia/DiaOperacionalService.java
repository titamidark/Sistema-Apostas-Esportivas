package br.com.bancapro.application.dia;

import br.com.bancapro.aposta.Aposta;
import br.com.bancapro.aposta.ApostaRepository;
import br.com.bancapro.aposta.ResultadoAposta;
import br.com.bancapro.aposta.StatusAposta;
import br.com.bancapro.application.caixa.CaixaService;
import br.com.bancapro.application.dia.dto.DiaOperacionalDTO;
import br.com.bancapro.dia.DiaOperacional;
import br.com.bancapro.dia.DiaOperacionalRepository;
import br.com.bancapro.dia.StatusDiaOperacional;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class DiaOperacionalService {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    private final DiaOperacionalRepository diaRepository;
    private final CaixaService caixaService;
    private final ApostaRepository apostaRepository;

    public DiaOperacionalService(
            DiaOperacionalRepository diaRepository,
            CaixaService caixaService,
            ApostaRepository apostaRepository
    ) {
        this.diaRepository = diaRepository;
        this.caixaService = caixaService;
        this.apostaRepository = apostaRepository;
    }

    public LocalDate hoje() {
        return LocalDate.now(ZONE_ID);
    }

    public DiaOperacional getOuCriarDiaAberto() {
        return diaRepository
                .findFirstByStatusOrderByDataDescIdDesc(StatusDiaOperacional.ABERTO)
                .orElseGet(() -> abrirDiaInterno(hoje(), BigDecimal.ZERO, false));
    }

    @Transactional
    public DiaOperacionalDTO abrirDia(LocalDate data, BigDecimal saldoInicial) {
        LocalDate dataOperacional = data == null ? hoje() : data;
        BigDecimal saldo = zero(saldoInicial);

        diaRepository.findFirstByStatusOrderByDataDescIdDesc(StatusDiaOperacional.ABERTO)
                .ifPresent(dia -> {
                    throw new RuntimeException(
                            "Já existe um dia operacional aberto: " + dia.getData()
                    );
                });

        DiaOperacional dia = abrirDiaInterno(dataOperacional, saldo, true);

        return DiaOperacionalDTO.fromEntity(atualizarResumoDia(dia));
    }

    private DiaOperacional abrirDiaInterno(
            LocalDate data,
            BigDecimal saldoInicial,
            boolean registrarCaixa
    ) {
        diaRepository.findByData(data).ifPresent(dia -> {
            throw new RuntimeException(
                    "Já existe dia operacional para a data " + data
            );
        });

        DiaOperacional dia = new DiaOperacional();
        dia.setData(data);
        dia.setStatus(StatusDiaOperacional.ABERTO);
        dia.setSaldoInicial(zero(saldoInicial));
        dia.setSaldoFinal(zero(saldoInicial));
        dia.setAbertoEm(LocalDateTime.now());

        DiaOperacional salvo = diaRepository.save(dia);

        if (registrarCaixa && zero(saldoInicial).compareTo(BigDecimal.ZERO) > 0) {
            caixaService.movimentarNaData(
                    br.com.bancapro.caixa.TipoMovimentacaoCaixa.SALDO_INICIAL_DIA,
                    saldoInicial,
                    "Abertura do dia operacional " + data,
                    data
            );
        }

        return salvo;
    }

    @Transactional
    public DiaOperacionalDTO fecharDiaAtual() {
        DiaOperacional dia = getOuCriarDiaAberto();

        dia = atualizarResumoDia(dia);
        dia.setStatus(StatusDiaOperacional.FECHADO);
        dia.setFechadoEm(LocalDateTime.now());

        return DiaOperacionalDTO.fromEntity(diaRepository.save(dia));
    }

    @Transactional
    public DiaOperacionalDTO iniciarProximoDia() {
        DiaOperacional ultimoFechado = diaRepository
                .findFirstByStatusOrderByDataDescFechadoEmDesc(StatusDiaOperacional.FECHADO)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Nenhum dia fechado encontrado para iniciar o próximo dia"
                        )
                );

        LocalDate proximaData = ultimoFechado.getData().plusDays(1);

        return abrirDia(proximaData, BigDecimal.ZERO);
    }

    public DiaOperacionalDTO resumoDiaAtual() {
        DiaOperacional dia = getOuCriarDiaAberto();

        return DiaOperacionalDTO.fromEntity(atualizarResumoDia(dia));
    }

    public List<DiaOperacionalDTO> historico() {
        return diaRepository.findTop30ByOrderByDataDesc()
                .stream()
                .map(DiaOperacionalDTO::fromEntity)
                .toList();
    }

    @Transactional
    public DiaOperacional atualizarResumoDia(DiaOperacional dia) {
        LocalDate data = dia.getData();

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();

        BigDecimal saldoFinal = caixaService.resumoPorData(data).getSaldoAtual();

        List<Aposta> apostasDoDia =
                apostaRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(inicio, fim);

        BigDecimal totalApostado = BigDecimal.ZERO;
        BigDecimal totalGreen = BigDecimal.ZERO;
        BigDecimal totalRed = BigDecimal.ZERO;
        BigDecimal totalCashout = BigDecimal.ZERO;
        BigDecimal lucroPrejuizo = BigDecimal.ZERO;

        for (Aposta aposta : apostasDoDia) {
            BigDecimal valorApostado = zero(aposta.getValorApostado());
            BigDecimal lucro = zero(aposta.getLucroPrejuizo());

            if (
                    aposta.getStatus() == StatusAposta.APOSTA_FEITA ||
                            aposta.getStatus() == StatusAposta.EM_ANDAMENTO ||
                            aposta.getStatus() == StatusAposta.FECHADA
            ) {
                totalApostado = totalApostado.add(valorApostado);
            }

            if (aposta.getStatus() == StatusAposta.FECHADA && aposta.getResultado() != null) {
                ResultadoAposta resultado = aposta.getResultado();

                if (resultado == ResultadoAposta.GREEN) {
                    BigDecimal lucroGreen = lucro.max(BigDecimal.ZERO);

                    totalGreen = totalGreen.add(lucroGreen);
                    lucroPrejuizo = lucroPrejuizo.add(lucroGreen);
                }

                if (resultado == ResultadoAposta.RED) {
                    BigDecimal perda = lucro.compareTo(BigDecimal.ZERO) < 0
                            ? lucro.abs()
                            : valorApostado;

                    totalRed = totalRed.add(perda);
                    lucroPrejuizo = lucroPrejuizo.subtract(perda);
                }

                if (resultado == ResultadoAposta.CASHOUT) {
                    totalCashout = totalCashout.add(lucro);
                    lucroPrejuizo = lucroPrejuizo.add(lucro);
                }
            }
        }

        BigDecimal roi = BigDecimal.ZERO;

        if (totalApostado.compareTo(BigDecimal.ZERO) > 0) {
            roi = lucroPrejuizo
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            totalApostado,
                            2,
                            BigDecimal.ROUND_HALF_UP
                    );
        }

        long pendentes = apostasDoDia.stream()
                .filter(aposta -> aposta.getStatus() == StatusAposta.AGENDADA)
                .count();

        dia.setSaldoFinal(saldoFinal);
        dia.setTotalApostado(totalApostado);
        dia.setTotalGreen(totalGreen);
        dia.setTotalRed(totalRed);
        dia.setTotalCashout(totalCashout);
        dia.setLucroPrejuizo(lucroPrejuizo);
        dia.setRoi(roi);
        dia.setQuantidadeApostas((long) apostasDoDia.size());
        dia.setQuantidadePendentes(pendentes);

        return diaRepository.save(dia);
    }

    private BigDecimal zero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}