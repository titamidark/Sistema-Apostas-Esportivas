package br.com.bancapro.application.cofrinho;

import br.com.bancapro.application.cofrinho.dto.AdicionarGastoCofrinhoDTO;
import br.com.bancapro.application.cofrinho.dto.CofrinhoRequestDTO;
import br.com.bancapro.application.cofrinho.dto.MarcarDiaCofrinhoDTO;
import br.com.bancapro.cofrinho.Cofrinho;
import br.com.bancapro.cofrinho.CofrinhoDia;
import br.com.bancapro.cofrinho.CofrinhoDiaRepository;
import br.com.bancapro.cofrinho.CofrinhoRepository;
import br.com.bancapro.cofrinho.StatusCofrinhoDia;
import br.com.bancapro.cofrinho.TipoCofrinho;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CofrinhoService {

    private final CofrinhoRepository cofrinhoRepository;
    private final CofrinhoDiaRepository cofrinhoDiaRepository;

    public CofrinhoService(
            CofrinhoRepository cofrinhoRepository,
            CofrinhoDiaRepository cofrinhoDiaRepository
    ) {
        this.cofrinhoRepository = cofrinhoRepository;
        this.cofrinhoDiaRepository = cofrinhoDiaRepository;
    }

    public List<Cofrinho> listar() {
        return cofrinhoRepository.findAll();
    }

    public Cofrinho buscarPorId(Long id) {
        return cofrinhoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cofrinho não encontrado")
                );
    }

    public List<CofrinhoDia> listarDias(Long cofrinhoId) {
        buscarPorId(cofrinhoId);

        return cofrinhoDiaRepository
                .findByCofrinhoIdOrderByDiaAsc(cofrinhoId);
    }

    @Transactional
    public Cofrinho criar(CofrinhoRequestDTO dto) {
        validarCriacao(dto);

        LocalDate dataInicio =
                dto.getDataInicio() == null
                        ? LocalDate.now()
                        : dto.getDataInicio();

        TipoCofrinho tipo =
                dto.getTipo() == null
                        ? TipoCofrinho.META_FIXA
                        : dto.getTipo();

        BigDecimal valorTotal = zero(dto.getValorTotal());

        BigDecimal valorReservadoInicial =
                zero(dto.getValorReservadoInicial());

        validarReservaInicial(
                valorTotal,
                valorReservadoInicial
        );

        Cofrinho cofrinho = new Cofrinho();

        cofrinho.setNome(dto.getNome().trim());
        cofrinho.setDescricao(dto.getDescricao());
        cofrinho.setTipo(tipo);

        cofrinho.setValorTotal(valorTotal);
        cofrinho.setValorOriginal(valorTotal);
        cofrinho.setValorReservadoInicial(
                valorReservadoInicial
        );

        cofrinho.setDias(dto.getDias());
        cofrinho.setDataInicio(dataInicio);
        cofrinho.setDataFinal(
                dataInicio.plusDays(dto.getDias() - 1L)
        );

        Cofrinho salvo =
                cofrinhoRepository.save(cofrinho);

        BigDecimal valorRestante =
                valorTotal
                        .subtract(valorReservadoInicial)
                        .max(BigDecimal.ZERO);

        criarDiasIniciais(
                salvo.getId(),
                dataInicio,
                dto.getDias(),
                valorRestante
        );

        recalcular(salvo.getId());

        return buscarPorId(salvo.getId());
    }

    private void criarDiasIniciais(
            Long cofrinhoId,
            LocalDate dataInicio,
            Integer quantidadeDias,
            BigDecimal valorRestante
    ) {
        BigDecimal valorBase =
                quantidadeDias > 0
                        ? valorRestante.divide(
                        BigDecimal.valueOf(quantidadeDias),
                        2,
                        RoundingMode.DOWN
                )
                        : BigDecimal.ZERO;

        BigDecimal totalBase =
                valorBase.multiply(
                        BigDecimal.valueOf(quantidadeDias)
                );

        BigDecimal diferencaUltimoDia =
                valorRestante.subtract(totalBase);

        for (int i = 1; i <= quantidadeDias; i++) {
            BigDecimal valorPrevisto = valorBase;

            if (i == quantidadeDias) {
                valorPrevisto =
                        valorPrevisto.add(diferencaUltimoDia);
            }

            CofrinhoDia dia = new CofrinhoDia();

            dia.setCofrinhoId(cofrinhoId);
            dia.setDia(i);
            dia.setData(dataInicio.plusDays(i - 1L));
            dia.setStatus(StatusCofrinhoDia.PENDENTE);
            dia.setValorPrevisto(
                    valorPrevisto.setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
            );
            dia.setValorDepositado(BigDecimal.ZERO);

            cofrinhoDiaRepository.save(dia);
        }
    }

    @Transactional
    public void deletar(Long id) {
        Cofrinho cofrinho = buscarPorId(id);

        cofrinhoDiaRepository.deletarPorCofrinhoId(id);
        cofrinhoRepository.delete(cofrinho);
    }

    @Transactional
    public CofrinhoDia marcarDia(
            Long cofrinhoId,
            Long diaId,
            MarcarDiaCofrinhoDTO dto
    ) {
        buscarPorId(cofrinhoId);

        CofrinhoDia dia =
                cofrinhoDiaRepository.findById(diaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dia do cofrinho não encontrado"
                                )
                        );

        if (!dia.getCofrinhoId().equals(cofrinhoId)) {
            throw new RuntimeException(
                    "Dia não pertence a este cofrinho"
            );
        }

        if (dto.getStatus() == null) {
            throw new RuntimeException("Informe o status");
        }

        switch (dto.getStatus()) {
            case GREEN -> {
                BigDecimal valor =
                        dto.getValorDepositado() == null
                                ? zero(dia.getValorPrevisto())
                                : dto.getValorDepositado();

                if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException(
                            "Valor depositado deve ser maior que zero"
                    );
                }

                dia.setStatus(StatusCofrinhoDia.GREEN);
                dia.setValorDepositado(
                        valor.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
            }

            case RED -> {
                dia.setStatus(StatusCofrinhoDia.RED);
                dia.setValorDepositado(BigDecimal.ZERO);
            }

            case PENDENTE -> {
                dia.setStatus(StatusCofrinhoDia.PENDENTE);
                dia.setValorDepositado(BigDecimal.ZERO);
            }
        }

        CofrinhoDia salvo =
                cofrinhoDiaRepository.save(dia);

        recalcular(cofrinhoId);

        return salvo;
    }

    @Transactional
    public Cofrinho adicionarGasto(
            Long cofrinhoId,
            AdicionarGastoCofrinhoDTO dto
    ) {
        Cofrinho cofrinho = buscarPorId(cofrinhoId);

        if (cofrinho.getTipo() != TipoCofrinho.VALOR_VARIAVEL) {
            throw new RuntimeException(
                    "Só é possível adicionar gasto em cofrinho de valor variável"
            );
        }

        if (
                dto.getValor() == null ||
                        dto.getValor().compareTo(BigDecimal.ZERO) <= 0
        ) {
            throw new RuntimeException(
                    "Informe um valor de gasto válido"
            );
        }

        BigDecimal novoValorTotal =
                zero(cofrinho.getValorTotal())
                        .add(dto.getValor())
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        cofrinho.setValorTotal(novoValorTotal);
        cofrinhoRepository.save(cofrinho);

        recalcular(cofrinhoId);

        return buscarPorId(cofrinhoId);
    }

    @Transactional
    public void recalcular(Long cofrinhoId) {
        Cofrinho cofrinho = buscarPorId(cofrinhoId);

        List<CofrinhoDia> dias =
                cofrinhoDiaRepository
                        .findByCofrinhoIdOrderByDiaAsc(cofrinhoId);

        BigDecimal depositadoNosDias =
                calcularDepositadoNosDias(dias);

        BigDecimal reservaInicial =
                zero(cofrinho.getValorReservadoInicial());

        BigDecimal totalReservado =
                reservaInicial.add(depositadoNosDias);

        BigDecimal falta =
                zero(cofrinho.getValorTotal())
                        .subtract(totalReservado)
                        .max(BigDecimal.ZERO)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        List<CofrinhoDia> diasPendentes =
                dias.stream()
                        .filter(dia ->
                                dia.getStatus() ==
                                        StatusCofrinhoDia.PENDENTE
                        )
                        .toList();

        redistribuirValorPendente(
                diasPendentes,
                falta
        );
    }

    private void redistribuirValorPendente(
            List<CofrinhoDia> diasPendentes,
            BigDecimal falta
    ) {
        if (diasPendentes.isEmpty()) {
            return;
        }

        int quantidade = diasPendentes.size();

        BigDecimal valorBase =
                falta.divide(
                        BigDecimal.valueOf(quantidade),
                        2,
                        RoundingMode.DOWN
                );

        BigDecimal totalBase =
                valorBase.multiply(
                        BigDecimal.valueOf(quantidade)
                );

        BigDecimal diferencaUltimoDia =
                falta.subtract(totalBase);

        for (int i = 0; i < quantidade; i++) {
            CofrinhoDia dia = diasPendentes.get(i);

            BigDecimal valorPrevisto = valorBase;

            if (i == quantidade - 1) {
                valorPrevisto =
                        valorPrevisto.add(diferencaUltimoDia);
            }

            dia.setValorPrevisto(
                    valorPrevisto.setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
            );

            cofrinhoDiaRepository.save(dia);
        }
    }

    @Transactional
    public void alterarDiasRestantes(
            Long cofrinhoId,
            Integer diasRestantes
    ) {
        if (diasRestantes == null || diasRestantes <= 0) {
            throw new RuntimeException(
                    "Informe a quantidade de dias restantes."
            );
        }

        Cofrinho cofrinho = buscarPorId(cofrinhoId);

        List<CofrinhoDia> dias =
                cofrinhoDiaRepository
                        .findByCofrinhoIdOrderByDiaAsc(cofrinhoId);

        List<CofrinhoDia> diasFixos =
                dias.stream()
                        .filter(dia ->
                                dia.getStatus() !=
                                        StatusCofrinhoDia.PENDENTE
                        )
                        .toList();

        List<CofrinhoDia> diasPendentesAtuais =
                dias.stream()
                        .filter(dia ->
                                dia.getStatus() ==
                                        StatusCofrinhoDia.PENDENTE
                        )
                        .toList();

        cofrinhoDiaRepository.deleteAll(
                diasPendentesAtuais
        );

        BigDecimal depositadoNosDias =
                calcularDepositadoNosDias(diasFixos);

        BigDecimal reservaInicial =
                zero(cofrinho.getValorReservadoInicial());

        BigDecimal totalReservado =
                reservaInicial.add(depositadoNosDias);

        BigDecimal falta =
                zero(cofrinho.getValorTotal())
                        .subtract(totalReservado)
                        .max(BigDecimal.ZERO)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        LocalDate dataInicioNovosDias;

        if (diasFixos.isEmpty()) {
            dataInicioNovosDias =
                    cofrinho.getDataInicio();
        } else {
            LocalDate ultimaData =
                    diasFixos.stream()
                            .map(CofrinhoDia::getData)
                            .max(LocalDate::compareTo)
                            .orElse(cofrinho.getDataInicio());

            dataInicioNovosDias =
                    ultimaData.plusDays(1);
        }

        int proximoNumeroDia =
                diasFixos.stream()
                        .map(CofrinhoDia::getDia)
                        .max(Integer::compareTo)
                        .orElse(0) + 1;

        List<CofrinhoDia> novosDias =
                new ArrayList<>();

        for (int i = 0; i < diasRestantes; i++) {
            CofrinhoDia novoDia = new CofrinhoDia();

            novoDia.setCofrinhoId(cofrinhoId);
            novoDia.setDia(proximoNumeroDia + i);
            novoDia.setData(
                    dataInicioNovosDias.plusDays(i)
            );
            novoDia.setStatus(
                    StatusCofrinhoDia.PENDENTE
            );
            novoDia.setValorPrevisto(BigDecimal.ZERO);
            novoDia.setValorDepositado(BigDecimal.ZERO);

            novosDias.add(
                    cofrinhoDiaRepository.save(novoDia)
            );
        }

        redistribuirValorPendente(
                novosDias,
                falta
        );

        cofrinho.setDias(
                diasFixos.size() + diasRestantes
        );

        cofrinho.setDataFinal(
                dataInicioNovosDias.plusDays(
                        diasRestantes - 1L
                )
        );

        cofrinhoRepository.save(cofrinho);

        recalcular(cofrinhoId);
    }

    private BigDecimal calcularDepositadoNosDias(
            List<CofrinhoDia> dias
    ) {
        return dias.stream()
                .filter(dia ->
                        dia.getStatus() ==
                                StatusCofrinhoDia.GREEN
                )
                .map(CofrinhoDia::getValorDepositado)
                .map(this::zero)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private void validarCriacao(
            CofrinhoRequestDTO dto
    ) {
        if (
                dto.getNome() == null ||
                        dto.getNome().isBlank()
        ) {
            throw new RuntimeException(
                    "Informe o nome do cofrinho"
            );
        }

        TipoCofrinho tipo =
                dto.getTipo() == null
                        ? TipoCofrinho.META_FIXA
                        : dto.getTipo();

        if (tipo == TipoCofrinho.META_FIXA) {
            if (
                    dto.getValorTotal() == null ||
                            dto.getValorTotal()
                                    .compareTo(BigDecimal.ZERO) <= 0
            ) {
                throw new RuntimeException(
                        "Informe o valor total da meta"
                );
            }
        }

        if (tipo == TipoCofrinho.VALOR_VARIAVEL) {
            if (
                    dto.getValorTotal() != null &&
                            dto.getValorTotal()
                                    .compareTo(BigDecimal.ZERO) < 0
            ) {
                throw new RuntimeException(
                        "O valor inicial não pode ser negativo"
                );
            }
        }

        if (
                dto.getDias() == null ||
                        dto.getDias() <= 0
        ) {
            throw new RuntimeException(
                    "Informe a quantidade de dias"
            );
        }
    }

    private void validarReservaInicial(
            BigDecimal valorTotal,
            BigDecimal valorReservadoInicial
    ) {
        if (
                valorReservadoInicial
                        .compareTo(BigDecimal.ZERO) < 0
        ) {
            throw new RuntimeException(
                    "O valor já reservado não pode ser negativo."
            );
        }

        if (
                valorReservadoInicial
                        .compareTo(valorTotal) > 0
        ) {
            throw new RuntimeException(
                    "O valor já reservado não pode ser maior que o valor total."
            );
        }
    }

    private BigDecimal zero(BigDecimal valor) {
        return valor == null
                ? BigDecimal.ZERO
                : valor;
    }
}