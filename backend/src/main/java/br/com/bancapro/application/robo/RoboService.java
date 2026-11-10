package br.com.bancapro.application.robo;

import br.com.bancapro.application.robo.dto.MetodoRoboRequestDTO;
import br.com.bancapro.application.robo.dto.RoboRequestDTO;
import br.com.bancapro.robo.MetodoRobo;
import br.com.bancapro.robo.MetodoRoboRepository;
import br.com.bancapro.robo.Robo;
import br.com.bancapro.robo.RoboRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoboService {

    private final RoboRepository roboRepository;
    private final MetodoRoboRepository metodoRepository;

    public RoboService(
            RoboRepository roboRepository,
            MetodoRoboRepository metodoRepository
    ) {
        this.roboRepository = roboRepository;
        this.metodoRepository = metodoRepository;
    }

    public List<Robo> listarRobos() {
        return roboRepository.findByAtivoTrueOrderByNomeAsc();
    }

    public Robo criarRobo(RoboRequestDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new RuntimeException("Informe o nome do robô");
        }

        Robo robo = new Robo();
        robo.setNome(dto.getNome().trim());
        robo.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());

        return roboRepository.save(robo);
    }

    public List<MetodoRobo> listarMetodos() {
        return metodoRepository.findByAtivoTrueOrderByNomeAsc();
    }

    public List<MetodoRobo> listarMetodosPorRobo(Long roboId) {
        return metodoRepository.findByRoboIdAndAtivoTrueOrderByNomeAsc(roboId);
    }

    public MetodoRobo criarMetodo(MetodoRoboRequestDTO dto) {
        if (dto.getRoboId() == null) {
            throw new RuntimeException("Informe o robô");
        }

        roboRepository.findById(dto.getRoboId())
                .orElseThrow(() -> new RuntimeException("Robô não encontrado"));

        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new RuntimeException("Informe o nome do método");
        }

        MetodoRobo metodo = new MetodoRobo();
        metodo.setRoboId(dto.getRoboId());
        metodo.setNome(dto.getNome().trim());
        metodo.setDescricao(dto.getDescricao());
        metodo.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());

        return metodoRepository.save(metodo);
    }
}