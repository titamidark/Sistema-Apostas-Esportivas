package br.com.bancapro.application.casa;

import br.com.bancapro.casa.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class CasaService {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final String DESC_ATUALIZACAO_SALDO = "Atualização de saldo";

    private final CasaRepository casaRepository;
    private final MovimentacaoCasaRepository movimentacaoRepository;

    public CasaService(
            CasaRepository casaRepository,
            MovimentacaoCasaRepository movimentacaoRepository
    ) {
        this.casaRepository = casaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<CasaAposta> listar() {
        List<CasaAposta> casas = casaRepository.findAll();

        for (CasaAposta casa : casas) {
            recalcularSaldo(casa.getId());
        }

        return casaRepository.findAll();
    }

    public CasaAposta buscarPorId(Long id) {
        return casaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Casa não encontrada"));
    }

    public CasaAposta criar(CasaAposta casa) {
        if (casa.getSaldoInicial() == null) {
            casa.setSaldoInicial(BigDecimal.ZERO);
        }

        casa.setSaldoAtual(casa.getSaldoInicial());

        CasaAposta salva = casaRepository.save(casa);

        MovimentacaoCasa mov = new MovimentacaoCasa();
        mov.setCasaId(salva.getId());
        mov.setTipo(TipoMovimentacaoCasa.SALDO_INICIAL);
        mov.setValor(salva.getSaldoInicial());
        mov.setDescricao("Saldo inicial da casa");

        movimentacaoRepository.save(mov);

        return salva;
    }

    public CasaAposta atualizar(Long id, CasaAposta dados) {
        CasaAposta casa = buscarPorId(id);

        boolean temLancamento =
                movimentacaoRepository.existsByCasaIdAndTipoNot(
                        id,
                        TipoMovimentacaoCasa.SALDO_INICIAL
                );

        casa.setNome(dados.getNome());
        casa.setAtiva(dados.getAtiva());
        casa.setLinkSite(dados.getLinkSite());

        if (!temLancamento) {
            casa.setSaldoInicial(dados.getSaldoInicial());
            casa.setSaldoAtual(dados.getSaldoInicial());

            List<MovimentacaoCasa> movimentos =
                    movimentacaoRepository.findByCasaIdOrderByDataMovimentacaoDesc(id);

            for (MovimentacaoCasa mov : movimentos) {
                if (mov.getTipo() == TipoMovimentacaoCasa.SALDO_INICIAL) {
                    mov.setValor(dados.getSaldoInicial());
                    movimentacaoRepository.save(mov);
                }
            }
        }

        return casaRepository.save(casa);
    }


    public CasaAposta atualizarSaldoAtual(
            Long casaId,
            BigDecimal novoSaldo,
            BigDecimal retiradaPessoal
    ) {

        if (novoSaldo == null || novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Informe um saldo válido.");
        }

        if (retiradaPessoal == null) {
            retiradaPessoal = BigDecimal.ZERO;
        }

        recalcularSaldo(casaId);

        CasaAposta casa = buscarPorId(casaId);

        BigDecimal saldoAnterior =
                casa.getSaldoAtual() == null
                        ? BigDecimal.ZERO
                        : casa.getSaldoAtual();

        /*
         * CASO ESPECIAL:
         * Casa zerada e usuário informa primeiro saldo.
         * Isso é ajuste operacional, não GREEN.
         */
        if (saldoAnterior.compareTo(BigDecimal.ZERO) == 0
                && novoSaldo.compareTo(BigDecimal.ZERO) > 0
                && retiradaPessoal.compareTo(BigDecimal.ZERO) == 0) {

            movimentar(
                    casaId,
                    TipoMovimentacaoCasa.AJUSTE_ENTRADA,
                    novoSaldo,
                    "Saldo inicial informado pela atualização"
            );

            recalcularSaldo(casaId);

            return buscarPorId(casaId);
        }

        BigDecimal saldoEsperado =
                saldoAnterior.subtract(retiradaPessoal);

        BigDecimal diferencaResultado =
                novoSaldo.subtract(saldoEsperado);

        if (retiradaPessoal.compareTo(BigDecimal.ZERO) > 0) {

            movimentar(
                    casaId,
                    TipoMovimentacaoCasa.SAQUE,
                    retiradaPessoal,
                    "Retirada pessoal para banco"
            );
        }

        if (diferencaResultado.compareTo(BigDecimal.ZERO) > 0) {

            movimentar(
                    casaId,
                    TipoMovimentacaoCasa.GREEN,
                    diferencaResultado,
                    DESC_ATUALIZACAO_SALDO +
                            " | GREEN +" +
                            diferencaResultado
            );
        }

        else if (diferencaResultado.compareTo(BigDecimal.ZERO) < 0) {

            movimentar(
                    casaId,
                    TipoMovimentacaoCasa.RED,
                    diferencaResultado.abs(),
                    DESC_ATUALIZACAO_SALDO +
                            " | RED -" +
                            diferencaResultado.abs()
            );
        }

        recalcularSaldo(casaId);

        return buscarPorId(casaId);
    }

    public BigDecimal resultadoAtualizacaoSaldoHoje(Long casaId) {
        CasaAposta casa = buscarPorId(casaId);

        LocalDate hoje = LocalDate.now(ZONE_ID);

        List<MovimentacaoCasa> movimentacoes =
                movimentacaoRepository.findByCasaIdOrderByDataMovimentacaoDesc(casaId);

        BigDecimal resultado = BigDecimal.ZERO;

        boolean ehFulltBet =
                casa.getNome() != null &&
                        casa.getNome().equalsIgnoreCase("FulltBet");

        for (MovimentacaoCasa mov : movimentacoes) {
            if (mov.getDataMovimentacao() == null) {
                continue;
            }

            if (!mov.getDataMovimentacao().toLocalDate().equals(hoje)) {
                continue;
            }

            boolean ehAtualizacaoSaldo =
                    mov.getDescricao() != null &&
                            mov.getDescricao().startsWith(DESC_ATUALIZACAO_SALDO);

            if (!ehAtualizacaoSaldo && !ehFulltBet) {
                continue;
            }

            BigDecimal valor = mov.getValor() == null
                    ? BigDecimal.ZERO
                    : mov.getValor();

            if (mov.getTipo() == TipoMovimentacaoCasa.GREEN) {
                resultado = resultado.add(valor);
            }

            if (mov.getTipo() == TipoMovimentacaoCasa.RED) {
                resultado = resultado.subtract(valor);
            }
        }

        return resultado;
    }
    public MovimentacaoCasa movimentar(
            Long casaId,
            TipoMovimentacaoCasa tipo,
            BigDecimal valor,
            String descricao
    ) {
        CasaAposta casa = buscarPorId(casaId);

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor da movimentação deve ser maior que zero");
        }

        MovimentacaoCasa mov = new MovimentacaoCasa();
        mov.setCasaId(casa.getId());
        mov.setTipo(tipo);
        mov.setValor(valor);
        mov.setDescricao(descricao);

        MovimentacaoCasa salva = movimentacaoRepository.save(mov);

        recalcularSaldo(casaId);

        return salva;
    }

    public List<MovimentacaoCasa> listarMovimentacoes(Long casaId) {
        buscarPorId(casaId);

        return movimentacaoRepository.findByCasaIdOrderByDataMovimentacaoDesc(casaId);
    }

    public void recalcularSaldo(Long casaId) {
        CasaAposta casa = buscarPorId(casaId);

        List<MovimentacaoCasa> movimentacoes =
                movimentacaoRepository.findByCasaIdOrderByDataMovimentacaoDesc(casaId);

        BigDecimal saldo = BigDecimal.ZERO;

        for (MovimentacaoCasa mov : movimentacoes) {

            BigDecimal valor =
                    mov.getValor() == null
                            ? BigDecimal.ZERO
                            : mov.getValor();

            switch (mov.getTipo()) {

                case SALDO_INICIAL:
                case DEPOSITO:
                case GREEN:
                case CASHOUT:
                case AJUSTE:
                case AJUSTE_ENTRADA:
                    saldo = saldo.add(valor);
                    break;

                case AJUSTE_SAIDA:
                    saldo = saldo.subtract(valor);
                    break;

                case SAQUE:
                case APOSTA:
                    saldo = saldo.subtract(valor);
                    break;

                case RED:

                    if (mov.getDescricao() != null
                            && mov.getDescricao().startsWith(DESC_ATUALIZACAO_SALDO)) {

                        saldo = saldo.subtract(valor);
                    }

                    break;

                default:
                    break;
            }
        }

        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            saldo = BigDecimal.ZERO;
        }

        casa.setSaldoAtual(saldo);
        casaRepository.save(casa);
    }


    public void deletar(Long id) {
        CasaAposta casa = buscarPorId(id);

        boolean temMovimentacao =
                !movimentacaoRepository
                        .findByCasaIdOrderByDataMovimentacaoDesc(id)
                        .isEmpty();

        if (temMovimentacao) {
            throw new RuntimeException(
                    "Não é possível excluir uma casa com movimentações. Inative a casa."
            );
        }

        casaRepository.delete(casa);
    }
}