package br.com.bancapro.application.alavancagem;

import br.com.bancapro.alavancagem.*;
import br.com.bancapro.aposta.Aposta;
import br.com.bancapro.aposta.ResultadoAposta;
import br.com.bancapro.application.alavancagem.dto.ProjetoAlavancagemRequestDTO;
import br.com.bancapro.application.alavancagem.dto.ProjetoAlavancagemResumoDTO;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlavancagemService {

    private final ProjetoAlavancagemRepository projetoRepository;
    private final CicloAlavancagemRepository cicloRepository;

    public AlavancagemService(
            ProjetoAlavancagemRepository projetoRepository,
            CicloAlavancagemRepository cicloRepository
    ) {
        this.projetoRepository = projetoRepository;
        this.cicloRepository = cicloRepository;
    }

    public List<ProjetoAlavancagemResumoDTO> listar() {
        return projetoRepository.findAllByOrderByIdDesc()
                .stream()
                .map(ProjetoAlavancagemResumoDTO::fromEntity)
                .toList();
    }

    public List<ProjetoAlavancagemResumoDTO> listarAtivos() {
        return projetoRepository
                .findByStatusOrderByIdDesc(StatusProjetoAlavancagem.ATIVO)
                .stream()
                .map(ProjetoAlavancagemResumoDTO::fromEntity)
                .toList();
    }

    public ProjetoAlavancagem buscarPorId(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto de alavancagem não encontrado"));
    }

    public BigDecimal proximaAposta(Long projetoId) {
        ProjetoAlavancagem projeto = buscarPorId(projetoId);
        return zero(projeto.getStakeAtual());
    }

    @Transactional
    public ProjetoAlavancagemResumoDTO criar(ProjetoAlavancagemRequestDTO dto) {
        validar(dto);

        ProjetoAlavancagem projeto = new ProjetoAlavancagem();
        projeto.setNome(dto.getNome().trim());
        projeto.setDescricao(dto.getDescricao());
        projeto.setValorInicial(zero(dto.getValorInicial()));
        projeto.setStakeAtual(zero(dto.getValorInicial()));
        projeto.setLucroCicloAtual(BigDecimal.ZERO);
        projeto.setRodadasPorCiclo(dto.getRodadasPorCiclo());
        projeto.setRodadaAtual(0);
        projeto.setPercentualReinvestimento(
                dto.getPercentualReinvestimento() == null
                        ? BigDecimal.valueOf(100)
                        : dto.getPercentualReinvestimento()
        );
        projeto.setCicloAtual(1);
        projeto.setLucroRetiradoAcumulado(BigDecimal.ZERO);
        projeto.setRedAcumulado(BigDecimal.ZERO);
        projeto.setResultadoLiquido(BigDecimal.ZERO);
        projeto.setCiclosGreen(0);
        projeto.setCiclosRed(0);
        projeto.setMaiorSequenciaGreen(0);
        projeto.setSequenciaAtualGreen(0);
        projeto.setMaiorStake(zero(dto.getValorInicial()));
        projeto.setStatus(StatusProjetoAlavancagem.ATIVO);

        ProjetoAlavancagem salvo = projetoRepository.save(projeto);

        abrirCiclo(salvo);

        return ProjetoAlavancagemResumoDTO.fromEntity(salvo);
    }

    @Transactional
    public void processarResultadoAposta(Aposta aposta) {
        if (aposta.getProjetoAlavancagemId() == null) {
            return;
        }

        ProjetoAlavancagem projeto =
                buscarPorId(aposta.getProjetoAlavancagemId());

        if (projeto.getStatus() != StatusProjetoAlavancagem.ATIVO) {
            return;
        }

        if (aposta.getResultado() == null ||
                aposta.getResultado() == ResultadoAposta.PENDENTE) {
            return;
        }

        if (aposta.getResultado() == ResultadoAposta.GREEN ||
                aposta.getResultado() == ResultadoAposta.CASHOUT) {
            processarGreenOuCashout(projeto, aposta);
            return;
        }

        if (aposta.getResultado() == ResultadoAposta.RED) {
            processarRed(projeto);
        }
    }

    private void processarGreenOuCashout(
            ProjetoAlavancagem projeto,
            Aposta aposta
    ) {
        BigDecimal lucro = zero(aposta.getLucroPrejuizo());

        if (lucro.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal percentual =
                zero(projeto.getPercentualReinvestimento())
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal valorReinvestido =
                lucro.multiply(percentual).setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorRetirado =
                lucro.subtract(valorReinvestido).setScale(2, RoundingMode.HALF_UP);

        projeto.setLucroCicloAtual(
                zero(projeto.getLucroCicloAtual()).add(lucro)
        );

        projeto.setStakeAtual(
                zero(projeto.getStakeAtual()).add(valorReinvestido)
        );

        projeto.setLucroRetiradoAcumulado(
                zero(projeto.getLucroRetiradoAcumulado()).add(valorRetirado)
        );

        projeto.setRodadaAtual(zeroInt(projeto.getRodadaAtual()) + 1);

        if (zero(aposta.getValorApostado()).compareTo(zero(projeto.getMaiorStake())) > 0) {
            projeto.setMaiorStake(zero(aposta.getValorApostado()));
        }

        atualizarCicloAberto(projeto, valorRetirado);

        if (zeroInt(projeto.getRodadaAtual()) >= zeroInt(projeto.getRodadasPorCiclo())) {
            fecharCicloGreen(projeto);
        } else {
            projetoRepository.save(projeto);
        }
    }

    private void processarRed(ProjetoAlavancagem projeto) {
        CicloAlavancagem ciclo = cicloAberto(projeto.getId());

        ciclo.setStatus(StatusCicloAlavancagem.RED);
        ciclo.setStakeFinal(BigDecimal.ZERO);
        ciclo.setLucroCiclo(zero(projeto.getLucroCicloAtual()));
        ciclo.setRodadasRealizadas(zeroInt(projeto.getRodadaAtual()));
        ciclo.setFechadoEm(LocalDateTime.now());

        cicloRepository.save(ciclo);

        projeto.setRedAcumulado(
                zero(projeto.getRedAcumulado()).add(zero(projeto.getStakeAtual()))
        );

        projeto.setCiclosRed(zeroInt(projeto.getCiclosRed()) + 1);
        projeto.setSequenciaAtualGreen(0);

        projeto.setCicloAtual(zeroInt(projeto.getCicloAtual()) + 1);
        projeto.setStakeAtual(zero(projeto.getValorInicial()));
        projeto.setLucroCicloAtual(BigDecimal.ZERO);
        projeto.setRodadaAtual(0);

        projetoRepository.save(projeto);

        abrirCiclo(projeto);
    }

    private void fecharCicloGreen(ProjetoAlavancagem projeto) {
        CicloAlavancagem ciclo = cicloAberto(projeto.getId());

        ciclo.setStatus(StatusCicloAlavancagem.GREEN);
        ciclo.setStakeFinal(zero(projeto.getStakeAtual()));
        ciclo.setLucroCiclo(zero(projeto.getLucroCicloAtual()));
        ciclo.setRodadasRealizadas(zeroInt(projeto.getRodadaAtual()));
        ciclo.setFechadoEm(LocalDateTime.now());

        cicloRepository.save(ciclo);

        projeto.setCiclosGreen(zeroInt(projeto.getCiclosGreen()) + 1);
        projeto.setSequenciaAtualGreen(zeroInt(projeto.getSequenciaAtualGreen()) + 1);

        if (zeroInt(projeto.getSequenciaAtualGreen()) >
                zeroInt(projeto.getMaiorSequenciaGreen())) {
            projeto.setMaiorSequenciaGreen(projeto.getSequenciaAtualGreen());
        }

        projeto.setCicloAtual(zeroInt(projeto.getCicloAtual()) + 1);
        projeto.setLucroCicloAtual(BigDecimal.ZERO);
        projeto.setRodadaAtual(0);

        projetoRepository.save(projeto);

        abrirCiclo(projeto);
    }

    private CicloAlavancagem abrirCiclo(ProjetoAlavancagem projeto) {
        CicloAlavancagem ciclo = new CicloAlavancagem();

        ciclo.setProjetoId(projeto.getId());
        ciclo.setNumero(zeroInt(projeto.getCicloAtual()));
        ciclo.setValorInicial(zero(projeto.getValorInicial()));
        ciclo.setStakeInicial(zero(projeto.getStakeAtual()));
        ciclo.setStakeFinal(zero(projeto.getStakeAtual()));
        ciclo.setLucroCiclo(BigDecimal.ZERO);
        ciclo.setValorRetirado(BigDecimal.ZERO);
        ciclo.setRodadasPlanejadas(zeroInt(projeto.getRodadasPorCiclo()));
        ciclo.setRodadasRealizadas(0);
        ciclo.setStatus(StatusCicloAlavancagem.ABERTO);

        return cicloRepository.save(ciclo);
    }

    private CicloAlavancagem cicloAberto(Long projetoId) {
        return cicloRepository
                .findFirstByProjetoIdAndStatusOrderByNumeroDesc(
                        projetoId,
                        StatusCicloAlavancagem.ABERTO
                )
                .orElseThrow(() ->
                        new RuntimeException("Ciclo aberto não encontrado")
                );
    }

    private void atualizarCicloAberto(
            ProjetoAlavancagem projeto,
            BigDecimal valorRetirado
    ) {
        CicloAlavancagem ciclo = cicloAberto(projeto.getId());

        ciclo.setStakeFinal(zero(projeto.getStakeAtual()));
        ciclo.setLucroCiclo(zero(projeto.getLucroCicloAtual()));
        ciclo.setValorRetirado(
                zero(ciclo.getValorRetirado()).add(zero(valorRetirado))
        );
        ciclo.setRodadasRealizadas(zeroInt(projeto.getRodadaAtual()));

        cicloRepository.save(ciclo);
    }

    public List<CicloAlavancagem> listarCiclos(Long projetoId) {
        buscarPorId(projetoId);
        return cicloRepository.findByProjetoIdOrderByNumeroDesc(projetoId);
    }

    private void validar(ProjetoAlavancagemRequestDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new RuntimeException("Informe o nome do projeto");
        }

        if (dto.getValorInicial() == null ||
                dto.getValorInicial().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Informe o valor inicial");
        }

        if (dto.getRodadasPorCiclo() == null || dto.getRodadasPorCiclo() <= 0) {
            throw new RuntimeException("Informe a quantidade de rodadas por ciclo");
        }

        if (dto.getPercentualReinvestimento() == null) {
            dto.setPercentualReinvestimento(BigDecimal.valueOf(100));
        }

        if (dto.getPercentualReinvestimento().compareTo(BigDecimal.ZERO) < 0 ||
                dto.getPercentualReinvestimento().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Percentual de reinvestimento deve estar entre 0 e 100");
        }
    }

    private BigDecimal zero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private Integer zeroInt(Integer valor) {
        return valor == null ? 0 : valor;
    }
}