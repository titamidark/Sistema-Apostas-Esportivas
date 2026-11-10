package br.com.bancapro.application.bilhete;

import br.com.bancapro.adapter.out.persistence.BilheteRepository;
import br.com.bancapro.application.bilhete.dto.CriarBilheteItemRequest;
import br.com.bancapro.application.bilhete.dto.CriarBilheteRequest;
import br.com.bancapro.casa.CasaAposta;
import br.com.bancapro.casa.CasaRepository;
import br.com.bancapro.domain.bilhete.Bilhete;
import br.com.bancapro.domain.bilhete.BilheteItem;
import br.com.bancapro.domain.bilhete.BilheteResultado;
import br.com.bancapro.domain.bilhete.BilheteStatus;
import br.com.bancapro.domain.bilhete.BilheteTipo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BilheteService {

    private final BilheteRepository bilheteRepository;
    private final CasaRepository casaRepository;

    public BilheteService(
            BilheteRepository bilheteRepository,
            CasaRepository casaRepository
    ) {
        this.bilheteRepository = bilheteRepository;
        this.casaRepository = casaRepository;
    }

    public Bilhete criar(CriarBilheteRequest request) {

        CasaAposta casa = casaRepository.findById(request.getCasaId())
                .orElseThrow(() -> new RuntimeException("Casa de aposta não encontrada"));

        Bilhete bilhete = new Bilhete();

        bilhete.setCasa(casa);
        bilhete.setTipo(request.getTipo());
        bilhete.setValorApostado(request.getValorApostado());
        bilhete.setTipster(request.getTipster());
        bilhete.setLinkTipster(request.getLinkTipster());
        bilhete.setObservacao(request.getObservacao());
        bilhete.setResultado(BilheteResultado.PENDENTE);

        if (request.getTipo() == BilheteTipo.RAPIDA) {
            bilhete.setStatus(BilheteStatus.APOSTA_FEITA);
        } else {
            bilhete.setStatus(BilheteStatus.AGENDADA);
        }

        BigDecimal oddTotal = BigDecimal.ONE;

        for (CriarBilheteItemRequest itemRequest : request.getItens()) {

            BilheteItem item = new BilheteItem();

            item.setDescricao(itemRequest.getDescricao());
            item.setMercado(itemRequest.getMercado());
            item.setOdd(itemRequest.getOdd());
            item.setBilhete(bilhete);

            bilhete.getItens().add(item);

            if (itemRequest.getOdd() != null) {
                oddTotal = oddTotal.multiply(itemRequest.getOdd());
            }
        }

        bilhete.setOddTotal(oddTotal);
        bilhete.calcularValores();

        return bilheteRepository.save(bilhete);
    }

    public List<Bilhete> listar() {
        return bilheteRepository.findAll();
    }
}