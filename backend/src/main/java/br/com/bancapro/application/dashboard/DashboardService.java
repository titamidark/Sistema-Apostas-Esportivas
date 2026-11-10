package br.com.bancapro.application.dashboard;

import br.com.bancapro.aposta.Aposta;
import br.com.bancapro.aposta.ApostaRepository;
import br.com.bancapro.aposta.StatusAposta;
import br.com.bancapro.application.caixa.CaixaService;
import br.com.bancapro.application.casa.CasaService;
import br.com.bancapro.application.dashboard.dto.DashboardResumoDTO;
import br.com.bancapro.application.dia.DiaOperacionalService;
import br.com.bancapro.application.dia.dto.DiaOperacionalDTO;
import br.com.bancapro.caixa.MovimentacaoCaixaRepository;
import br.com.bancapro.casa.CasaAposta;
import br.com.bancapro.casa.CasaRepository;
import br.com.bancapro.casa.MovimentacaoCasaRepository;
import br.com.bancapro.dia.DiaOperacionalRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final CaixaService caixaService;
    private final CasaService casaService;
    private final CasaRepository casaRepository;
    private final MovimentacaoCasaRepository movimentacaoCasaRepository;
    private final MovimentacaoCaixaRepository movimentacaoCaixaRepository;
    private final ApostaRepository apostaRepository;
    private final DiaOperacionalService diaOperacionalService;
    private final DiaOperacionalRepository diaOperacionalRepository;

    public DashboardService(
            CaixaService caixaService,
            CasaService casaService,
            CasaRepository casaRepository,
            MovimentacaoCasaRepository movimentacaoCasaRepository,
            MovimentacaoCaixaRepository movimentacaoCaixaRepository,
            ApostaRepository apostaRepository,
            DiaOperacionalService diaOperacionalService,
            DiaOperacionalRepository diaOperacionalRepository
    ) {
        this.caixaService = caixaService;
        this.casaService = casaService;
        this.casaRepository = casaRepository;
        this.movimentacaoCasaRepository = movimentacaoCasaRepository;
        this.movimentacaoCaixaRepository = movimentacaoCaixaRepository;
        this.apostaRepository = apostaRepository;
        this.diaOperacionalService = diaOperacionalService;
        this.diaOperacionalRepository = diaOperacionalRepository;
    }

    public DashboardResumoDTO resumo() {
        DashboardResumoDTO dto = new DashboardResumoDTO();

        BigDecimal caixaAtual = caixaService.resumo().getSaldoAtual();

        List<CasaAposta> casas = casaService.listar();

        BigDecimal saldoTotalCasas =
                casas.stream()
                        .map(casa -> zero(casa.getSaldoAtual()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        DiaOperacionalDTO dia = diaOperacionalService.resumoDiaAtual();

        BigDecimal totalApostado = zero(dia.getTotalApostado());


        BigDecimal resultadoAtualizacaoCasas =
                casas.stream()
                        .map(casa -> casaService.resultadoAtualizacaoSaldoHoje(casa.getId()))
                        .map(this::zero)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal lucroPrejuizo = zero(dia.getLucroPrejuizo());

        List<Aposta> apostas = apostaRepository.findAll();

        Long quantidadeAgendadas =
                apostas.stream()
                        .filter(aposta -> aposta.getStatus() == StatusAposta.AGENDADA)
                        .count();

        BigDecimal valorAgendadas =
                apostas.stream()
                        .filter(aposta -> aposta.getStatus() == StatusAposta.AGENDADA)
                        .map(aposta -> zero(aposta.getValorApostado()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer apostasEmAberto =
                (int) apostas.stream()
                        .filter(aposta ->
                                aposta.getStatus() == StatusAposta.APOSTA_FEITA ||
                                        aposta.getStatus() == StatusAposta.EM_ANDAMENTO
                        )
                        .count();

        BigDecimal recuperacaoPotencial =
                apostas.stream()
                        .filter(aposta ->
                                aposta.getStatus() == StatusAposta.APOSTA_FEITA ||
                                        aposta.getStatus() == StatusAposta.EM_ANDAMENTO
                        )
                        .map(aposta -> zero(aposta.getPossivelLucro()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal projecaoDia =
                lucroPrejuizo.add(recuperacaoPotencial);

        dto.setDiaOperacional(dia);
        dto.setCaixaAtual(caixaAtual);
        dto.setSaldoTotalCasas(saldoTotalCasas);
        dto.setPatrimonioTotal(caixaAtual.add(saldoTotalCasas));
        dto.setTotalApostado(totalApostado);
        dto.setLucroPrejuizo(lucroPrejuizo);
        dto.setQuantidadeJogosAgendados(quantidadeAgendadas);
        dto.setValorTotalJogosAgendados(valorAgendadas);

        dto.setApostasEmAberto(apostasEmAberto);
        dto.setRecuperacaoPotencial(recuperacaoPotencial);
        dto.setProjecaoDia(projecaoDia);

        return dto;
    }

    @Transactional
    public void zerarTudo() {
        apostaRepository.deleteAll();

        movimentacaoCaixaRepository.deleteAll();

        movimentacaoCasaRepository.deleteAll();

        diaOperacionalRepository.deleteAll();

        List<CasaAposta> casas = casaRepository.findAll();

        for (CasaAposta casa : casas) {
            casa.setSaldoInicial(BigDecimal.ZERO);
            casa.setSaldoAtual(BigDecimal.ZERO);

            casaRepository.save(casa);
        }
    }

    private BigDecimal zero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}