package com.thoni.epimanager.service;

import com.thoni.epimanager.entity.AtividadeEpi;
import com.thoni.epimanager.repository.AtividadeEpiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DataNormalizationService {

    private final AtividadeEpiRepository atividadeEpiRepository;

    public DataNormalizationService(AtividadeEpiRepository atividadeEpiRepository) {
        this.atividadeEpiRepository = atividadeEpiRepository;
    }

    @Transactional
    public void normalizeData() {
        System.out.println("==========================================");
        System.out.println("INICIANDO NORMALIZAÇÃO DE DADOS");
        System.out.println("==========================================");

        normalizeConditions();

        System.out.println("==========================================");
        System.out.println("NORMALIZAÇÃO CONCLUÍDA");
        System.out.println("==========================================");
    }

    private void normalizeConditions() {
        System.out.println("\n[1] Normalizando Condições de Uso...");
        List<AtividadeEpi> records = atividadeEpiRepository.findAll();
        int count = 0;

        for (AtividadeEpi record : records) {
            if (record.getCondicao() != null) {
                String original = record.getCondicao();
                String normalized = original;

                // Rule 1: Standardize "Trabalho em Altura"
                if (original.equalsIgnoreCase("Trabalho em altura superior a dois metros") ||
                        original.equalsIgnoreCase("Trabalho em altura")) {
                    normalized = "Trabalho em Altura";
                }

                // Rule 2: Trim whitespace
                normalized = normalized.trim();

                if (!normalized.equals(original)) {
                    record.setCondicao(normalized);
                    atividadeEpiRepository.save(record);
                    System.out.println(" -> Atualizado: '" + original + "' para '" + normalized + "'");
                    count++;
                }
            }
        }
        System.out.println(" -> Total de registros atualizados: " + count);
    }
}
