package br.com.bancapro.application.tipster;

import br.com.bancapro.application.tipster.dto.TipsterRequestDTO;
import br.com.bancapro.tipster.Tipster;
import br.com.bancapro.tipster.TipsterRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipsterService {

    private final TipsterRepository tipsterRepository;

    public TipsterService(TipsterRepository tipsterRepository) {
        this.tipsterRepository = tipsterRepository;
    }

    public List<Tipster> listar() {
        return tipsterRepository.findByAtivoTrueOrderByNomeAsc();
    }

    public Tipster criar(TipsterRequestDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new RuntimeException("Informe o nome do tipster");
        }

        Tipster tipster = new Tipster();
        tipster.setNome(dto.getNome().trim());
        tipster.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());

        return tipsterRepository.save(tipster);
    }
}