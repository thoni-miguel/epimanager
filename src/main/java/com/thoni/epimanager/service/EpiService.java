package com.thoni.epimanager.service;

import com.thoni.epimanager.entity.AtividadeEpi;
import com.thoni.epimanager.entity.Cargo;
import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.repository.AtividadeEpiRepository;
import com.thoni.epimanager.repository.CargoRepository;
import com.thoni.epimanager.repository.EpiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpiService {

    private final EpiRepository epiRepository;
    private final CargoRepository cargoRepository;
    private final AtividadeEpiRepository atividadeEpiRepository;

    public EpiService(EpiRepository epiRepository, CargoRepository cargoRepository,
            AtividadeEpiRepository atividadeEpiRepository) {
        this.epiRepository = epiRepository;
        this.cargoRepository = cargoRepository;
        this.atividadeEpiRepository = atividadeEpiRepository;
    }

    public List<Epi> findAll() {
        return epiRepository.findAll();
    }

    public List<Epi> findRecomendadosPorCargo(Long cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new RuntimeException("Cargo não encontrado"));

        List<AtividadeEpi> atividadeEpis = atividadeEpiRepository.findByAtividadeId(cargo.getAtividade().getId());

        return atividadeEpis.stream()
                .map(AtividadeEpi::getEpi)
                .collect(Collectors.toList());
    }

    public Epi save(Epi epi) {
        return epiRepository.save(epi);
    }
}
