package com.thoni.epimanager.service;

import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.repository.EpiRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpiService {

    private final EpiRepository epiRepository;

    public EpiService(EpiRepository epiRepository) {
        this.epiRepository = epiRepository;
    }

    public List<Epi> findAll() {
        return epiRepository.findAll();
    }

    public List<Epi> findRecommendedForRole(String role) {
        return epiRepository.findByAllowedRole(role);
    }

    public Epi save(Epi epi) {
        return epiRepository.save(epi);
    }
}
