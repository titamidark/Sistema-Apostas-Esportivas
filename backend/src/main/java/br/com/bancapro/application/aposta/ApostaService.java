package br.com.bancapro.application.aposta;

import br.com.bancapro.aposta.*;
import br.com.bancapro.aposta.dto.ApostaRequestDTO;
import br.com.bancapro.application.aposta.dto.FecharApostaRequestDTO;
import br.com.bancapro.application.caixa.CaixaService;
import br.com.bancapro.application.casa.CasaService;
import br.com.bancapro.caixa.TipoMovimentacaoCaixa;
import br.com.bancapro.casa.TipoMovimentacaoCasa;
import br.com.bancapro.application.alavancagem.AlavancagemService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ApostaService {

    private final ApostaRepository apostaRepository;
    private final CaixaService caixaService;
    private final CasaService casaService;

    private final AlavancagemService alavancagemService;

    public ApostaService(
            ApostaRepository apostaRepository,
            CaixaService caixaService,
            CasaService casaService,
            AlavancagemService alavancagemService
    ) {
        this.apostaRepository = apostaRepository;
        this.caixaService = caixaService;
        this.casaService = casaService;
        this.alavancagemService = alavancagemService;
    }


    public List<Aposta> listar() {
        return apostaRepository.findAllByOrderByCreatedAtDesc();
    }

    public Aposta buscarPorId(Long id) {
        return apostaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aposta não encontrada"));
    }

    @Transactional
    public Aposta criar(ApostaRequestDTO dto) {
        validar(dto);

        Aposta aposta = new Aposta();

        aposta.setCasaId(dto.getCasaId());
        aposta.setTipster(dto.getTipster());
        aposta.setOrigem(dto.getOrigem() == null ? "TIPSTER" : dto.getOrigem());
        aposta.setRoboId(dto.getRoboId());
        aposta.setMetodoRoboId(dto.getMetodoRoboId());
        aposta.setTipo(dto.getTipo());
        aposta.setMercado(dto.getMercado());
        aposta.setPalpite(dto.getPalpite());
        aposta.setValorApostado(zero(dto.getValorApostado()));
        aposta.setRetornoPrevisto(
                dto.getRetornoPrevisto() == null
                        ? BigDecimal.ZERO
                        : dto.getRetornoPrevisto()
        );        aposta.setOddTipster(zero(dto.getOddTipster()));
        aposta.setOddFeita(zero(dto.getOddFeita()));
        aposta.setLinkPrint(dto.getLinkPrint());
        aposta.setDataJogo(dto.getDataJogo());
        aposta.setResultado(ResultadoAposta.PENDENTE);
        aposta.setProjetoAlavancagemId(dto.getProjetoAlavancagemId());

        if (dto.getTipo() == TipoAposta.RAPIDA) {
            aposta.setStatus(StatusAposta.APOSTA_FEITA);
            aposta.calcularPrevistos();

            Aposta salva = apostaRepository.save(aposta);
            movimentarApostaFeita(salva);

            return salva;
        }

        aposta.setStatus(StatusAposta.AGENDADA);
        aposta.calcularPrevistos();

        return apostaRepository.save(aposta);
    }

    @Transactional
    public Aposta fazerAposta(Long id) {
        Aposta aposta = buscarPorId(id);

        if (aposta.getStatus() != StatusAposta.AGENDADA) {
            throw new RuntimeException("Somente apostas agendadas podem ser feitas");
        }

        aposta.setStatus(StatusAposta.APOSTA_FEITA);

        Aposta salva = apostaRepository.save(aposta);

        movimentarApostaFeita(salva);

        return salva;
    }

    @Transactional
    public Aposta fechar(Long id, FecharApostaRequestDTO dto) {
        Aposta aposta = buscarPorId(id);

        if (
                aposta.getStatus() != StatusAposta.APOSTA_FEITA &&
                        aposta.getStatus() != StatusAposta.EM_ANDAMENTO
        ) {
            throw new RuntimeException("A aposta precisa estar feita para ser fechada");
        }

        ResultadoAposta resultado = dto.getResultado();

        if (resultado == null || resultado == ResultadoAposta.PENDENTE) {
            throw new RuntimeException("Resultado inválido");
        }

        BigDecimal valorApostado = zero(aposta.getValorApostado());
        BigDecimal retornoReal;

        if (resultado == ResultadoAposta.GREEN) {
            retornoReal = zero(aposta.getRetornoPrevisto());
        } else if (resultado == ResultadoAposta.RED) {
            retornoReal = BigDecimal.ZERO;
        } else {
            retornoReal = zero(dto.getRetornoReal());
        }

        BigDecimal lucroPrejuizo = retornoReal.subtract(valorApostado);

        aposta.setResultado(resultado);
        aposta.setStatus(StatusAposta.FECHADA);
        aposta.setRetornoReal(retornoReal);
        aposta.setLucroPrejuizo(lucroPrejuizo);

        Aposta salva = apostaRepository.save(aposta);

        movimentarFechamento(salva);

        alavancagemService.processarResultadoAposta(salva);

        return salva;
    }


    @Transactional
    public Aposta cancelar(Long id) {
        Aposta aposta = buscarPorId(id);

        if (aposta.getStatus() == StatusAposta.FECHADA) {
            throw new RuntimeException("Não é possível cancelar uma aposta fechada");
        }

        if (aposta.getStatus() == StatusAposta.CANCELADA) {
            throw new RuntimeException("Aposta já está cancelada");
        }

        if (
                aposta.getStatus() == StatusAposta.APOSTA_FEITA ||
                        aposta.getStatus() == StatusAposta.EM_ANDAMENTO
        ) {
            BigDecimal valorEstorno = zero(aposta.getValorApostado());

            // A casa devolve o valor da aposta para o saldo dela, entao o estorno
            // volta para a casa (e nao para o caixa).
            if (valorEstorno.compareTo(BigDecimal.ZERO) > 0) {
                casaService.movimentar(
                        aposta.getCasaId(),
                        TipoMovimentacaoCasa.AJUSTE_ENTRADA,
                        valorEstorno,
                        "Estorno cancelamento aposta #" + aposta.getId()
                );
            }
        }

        aposta.setStatus(StatusAposta.CANCELADA);
        aposta.setResultado(ResultadoAposta.PENDENTE);
        aposta.setRetornoReal(BigDecimal.ZERO);
        aposta.setLucroPrejuizo(BigDecimal.ZERO);

        return apostaRepository.save(aposta);
    }

    private void movimentarApostaFeita(Aposta aposta) {
        BigDecimal valorAposta = zero(aposta.getValorApostado());

        BigDecimal saldoCasa =
                zero(casaService.buscarPorId(aposta.getCasaId()).getSaldoAtual());

        if (saldoCasa.compareTo(valorAposta) < 0) {

            BigDecimal faltaNaCasa = valorAposta.subtract(saldoCasa);

            BigDecimal saldoCaixa = zero(caixaService.saldoAtual());

            if (saldoCaixa.compareTo(faltaNaCasa) < 0) {

                BigDecimal totalDisponivel =
                        saldoCasa.add(saldoCaixa);

                BigDecimal faltaReal =
                        valorAposta.subtract(totalDisponivel);

                throw new RuntimeException(
                        "Saldo insuficiente para fazer a aposta.\n" +
                                "Valor da aposta: R$ " + valorAposta + "\n" +
                                "Saldo da casa: R$ " + saldoCasa + "\n" +
                                "Saldo do caixa: R$ " + saldoCaixa + "\n" +
                                "Disponível: R$ " + totalDisponivel + "\n" +
                                "Faltam: R$ " + faltaReal
                );            }
            caixaService.movimentar(
                    TipoMovimentacaoCaixa.DEPOSITO_CASA,
                    faltaNaCasa,
                    "Depósito automático para aposta #" + aposta.getId()
            );

            casaService.movimentar(
                    aposta.getCasaId(),
                    TipoMovimentacaoCasa.DEPOSITO,
                    faltaNaCasa,
                    "Depósito automático vindo do caixa para aposta #" + aposta.getId()
            );
        }

        casaService.movimentar(
                aposta.getCasaId(),
                TipoMovimentacaoCasa.APOSTA,
                valorAposta,
                "Aposta #" + aposta.getId()
        );
    }

    private void desfazerFechamento(Aposta aposta) {
        ResultadoAposta resultado = aposta.getResultado();

        // GREEN e CASHOUT creditaram o retorno na casa. RED nao alterou o saldo
        // (o valor ja tinha sido descontado ao fazer a aposta), entao nao ha o que desfazer.
        if (resultado == ResultadoAposta.GREEN || resultado == ResultadoAposta.CASHOUT) {
            BigDecimal credito = zero(aposta.getRetornoReal());

            if (credito.compareTo(BigDecimal.ZERO) > 0) {
                casaService.movimentar(
                        aposta.getCasaId(),
                        TipoMovimentacaoCasa.AJUSTE_SAIDA,
                        credito,
                        "Reabertura aposta #" + aposta.getId()
                );
            }
        }
    }

    private void movimentarFechamento(Aposta aposta) {
        if (aposta.getResultado() == ResultadoAposta.GREEN) {
            casaService.movimentar(
                    aposta.getCasaId(),
                    TipoMovimentacaoCasa.GREEN,
                    zero(aposta.getRetornoReal()),
                    "Green aposta #" + aposta.getId()
            );
        }

        if (aposta.getResultado() == ResultadoAposta.RED) {
            casaService.movimentar(
                    aposta.getCasaId(),
                    TipoMovimentacaoCasa.RED,
                    zero(aposta.getValorApostado()),
                    "Red aposta #" + aposta.getId()
            );

            return;
        }


        if (aposta.getResultado() == ResultadoAposta.CASHOUT) {
            casaService.movimentar(
                    aposta.getCasaId(),
                    TipoMovimentacaoCasa.CASHOUT,
                    zero(aposta.getRetornoReal()),
                    "Cashout aposta #" + aposta.getId()
            );
        }
    }

    private void validar(ApostaRequestDTO dto) {
        if (dto.getCasaId() == null) {
            throw new RuntimeException("Informe a casa");
        }

        if (dto.getTipo() == null) {
            throw new RuntimeException("Informe o tipo da aposta");
        }

        if (dto.getMercado() == null || dto.getMercado().isBlank()) {
            throw new RuntimeException("Informe o mercado/palpite");
        }

        if (dto.getValorApostado() == null || dto.getValorApostado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor apostado deve ser maior que zero");
        }

        if (dto.getOddFeita() == null || dto.getOddFeita().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Odd feita deve ser maior que zero");
        }

        String origem = dto.getOrigem() == null ? "TIPSTER" : dto.getOrigem();

        if ("TIPSTER".equals(origem)) {
            if (dto.getTipster() == null || dto.getTipster().isBlank()) {
                throw new RuntimeException("Informe o tipster");
            }
        }

        if ("ROBO".equals(origem)) {
            if (dto.getRoboId() == null) {
                throw new RuntimeException("Informe o robô");
            }

            if (dto.getMetodoRoboId() == null) {
                throw new RuntimeException("Informe o método do robô");
            }
        }
    }

    private BigDecimal zero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }


    @Transactional
    public Aposta atualizar(Long id, ApostaRequestDTO dto) {
        validar(dto);

        Aposta aposta = buscarPorId(id);

        if (aposta.getStatus() == StatusAposta.FECHADA) {
            throw new RuntimeException("Não é possível editar uma aposta fechada");
        }

        BigDecimal valorAntigo = zero(aposta.getValorApostado());
        Long casaAntigaId = aposta.getCasaId();

        boolean apostaJaFeita =
                aposta.getStatus() == StatusAposta.APOSTA_FEITA ||
                        aposta.getStatus() == StatusAposta.EM_ANDAMENTO;

        aposta.setCasaId(dto.getCasaId());
        aposta.setTipster(dto.getTipster());
        aposta.setOrigem(dto.getOrigem() == null ? "TIPSTER" : dto.getOrigem());
        aposta.setRoboId(dto.getRoboId());
        aposta.setMetodoRoboId(dto.getMetodoRoboId());
        aposta.setTipo(dto.getTipo());
        aposta.setMercado(dto.getMercado());
        aposta.setPalpite(dto.getPalpite());
        aposta.setValorApostado(zero(dto.getValorApostado()));
        aposta.setProjetoAlavancagemId(dto.getProjetoAlavancagemId());

        aposta.setRetornoPrevisto(
                dto.getRetornoPrevisto() == null
                        ? BigDecimal.ZERO
                        : dto.getRetornoPrevisto()
        );

        aposta.setOddTipster(zero(dto.getOddTipster()));
        aposta.setOddFeita(zero(dto.getOddFeita()));
        aposta.setLinkPrint(dto.getLinkPrint());
        aposta.setDataJogo(dto.getDataJogo());

        BigDecimal retornoPrevisto = zero(aposta.getRetornoPrevisto());
        BigDecimal valorApostado = zero(aposta.getValorApostado());

        aposta.setLucroPrejuizo(retornoPrevisto.subtract(valorApostado));

        Aposta salva = apostaRepository.save(aposta);

        if (apostaJaFeita) {
            ajustarSaldoCasaNaEdicao(
                    salva,
                    casaAntigaId,
                    valorAntigo
            );
        }

        return salva;
    }


    private void ajustarSaldoCasaNaEdicao(
            Aposta aposta,
            Long casaAntigaId,
            BigDecimal valorAntigo
    ) {
        BigDecimal valorNovo = zero(aposta.getValorApostado());

        if (!casaAntigaId.equals(aposta.getCasaId())) {

            casaService.movimentar(
                    casaAntigaId,
                    TipoMovimentacaoCasa.AJUSTE_ENTRADA,
                    valorAntigo,
                    "Estorno edição aposta #" + aposta.getId()
            );

            casaService.movimentar(
                    aposta.getCasaId(),
                    TipoMovimentacaoCasa.APOSTA,
                    valorNovo,
                    "Ajuste edição aposta #" + aposta.getId()
            );

            return;
        }

        BigDecimal diferenca = valorNovo.subtract(valorAntigo);

        if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
            casaService.movimentar(
                    aposta.getCasaId(),
                    TipoMovimentacaoCasa.APOSTA,
                    diferenca,
                    "Ajuste aumento aposta #" + aposta.getId()
            );
        }

        if (diferenca.compareTo(BigDecimal.ZERO) < 0) {
            casaService.movimentar(
                    aposta.getCasaId(),
                    TipoMovimentacaoCasa.AJUSTE_ENTRADA,
                    diferenca.abs(),
                    "Estorno redução aposta #" + aposta.getId()
            );
        }
    }


    @Transactional
    public Aposta reabrir(Long id) {
        Aposta aposta = buscarPorId(id);

        if (aposta.getStatus() != StatusAposta.FECHADA) {
            throw new RuntimeException("Somente apostas fechadas podem ser reabertas");
        }

        if (aposta.getProjetoAlavancagemId() != null) {
            throw new RuntimeException(
                    "Esta aposta faz parte de um projeto de alavancagem e nao pode ser reaberta"
            );
        }

        desfazerFechamento(aposta);

        aposta.setStatus(StatusAposta.APOSTA_FEITA);
        aposta.setResultado(ResultadoAposta.PENDENTE);
        aposta.setRetornoReal(BigDecimal.ZERO);
        aposta.setLucroPrejuizo(BigDecimal.ZERO);

        return apostaRepository.save(aposta);
    }
}