package com.thoni.epimanager.service;

import com.thoni.epimanager.entity.AtividadeEpi;
import com.thoni.epimanager.entity.Cargo;
import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.repository.AtividadeEpiRepository;
import com.thoni.epimanager.repository.CargoRepository;
import com.thoni.epimanager.repository.EpiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataDiagnosticService {

    private final EpiRepository epiRepository;
    private final CargoRepository cargoRepository;
    private final AtividadeEpiRepository atividadeEpiRepository;

    public DataDiagnosticService(EpiRepository epiRepository, CargoRepository cargoRepository,
            AtividadeEpiRepository atividadeEpiRepository) {
        this.epiRepository = epiRepository;
        this.cargoRepository = cargoRepository;
        this.atividadeEpiRepository = atividadeEpiRepository;
    }

    @Transactional(readOnly = true)
    public void runDiagnostic() {
        System.out.println("==========================================");
        System.out.println("RELATÓRIO DE DIAGNÓSTICO DE DADOS");
        System.out.println("==========================================");

        checkDuplicateEpis();
        checkInvalidCAs();
        checkDuplicateCargos();
        checkInconsistentConditions();

        System.out.println("==========================================");
        System.out.println("FIM DO DIAGNÓSTICO");
        System.out.println("==========================================");
    }

    private void checkDuplicateEpis() {
        System.out.println("\n[1] Verificando EPIs Duplicados (Case Insensitive)...");
        List<Epi> epis = epiRepository.findAll();
        Map<String, List<Epi>> groupedByName = epis.stream()
                .collect(Collectors.groupingBy(epi -> epi.getNome().trim().toLowerCase()));

        boolean found = false;
        for (Map.Entry<String, List<Epi>> entry : groupedByName.entrySet()) {
            if (entry.getValue().size() > 1) {
                found = true;
                System.out.println(" -> Duplicata encontrada: '" + entry.getKey() + "'");
                for (Epi epi : entry.getValue()) {
                    System.out.println("    - ID: " + epi.getId() + " | Nome Original: " + epi.getNome() + " | CA: "
                            + epi.getCa());
                }
            }
        }
        if (!found)
            System.out.println(" -> Nenhuma duplicata exata encontrada.");
    }

    private void checkInvalidCAs() {
        System.out.println("\n[2] Verificando CAs Inválidos ou Genéricos...");
        List<Epi> epis = epiRepository.findAll();
        boolean found = false;
        for (Epi epi : epis) {
            if (epi.getCa() == null || epi.getCa().equals("00000") || epi.getCa().trim().isEmpty()) {
                found = true;
                System.out.println(
                        " -> CA Inválido: ID " + epi.getId() + " | Nome: " + epi.getNome() + " | CA: " + epi.getCa());
            }
        }
        if (!found)
            System.out.println(" -> Nenhum CA inválido encontrado.");
    }

    private void checkDuplicateCargos() {
        System.out.println("\n[3] Verificando Cargos Duplicados (por Atividade)...");
        List<Cargo> cargos = cargoRepository.findAll();
        // Group by Activity ID + Cargo Name
        Map<String, List<Cargo>> grouped = cargos.stream()
                .collect(Collectors.groupingBy(c -> c.getAtividade().getId() + "|" + c.getNome().trim().toLowerCase()));

        boolean found = false;
        for (Map.Entry<String, List<Cargo>> entry : grouped.entrySet()) {
            if (entry.getValue().size() > 1) {
                found = true;
                Cargo first = entry.getValue().get(0);
                System.out.println(" -> Cargo Duplicado na Atividade '" + first.getAtividade().getNome() + "': '"
                        + first.getNome() + "'");
                for (Cargo c : entry.getValue()) {
                    System.out.println("    - ID: " + c.getId() + " | Nome Original: " + c.getNome());
                }
            }
        }
        if (!found)
            System.out.println(" -> Nenhum cargo duplicado encontrado.");
    }

    private void checkInconsistentConditions() {
        System.out.println("\n[4] Verificando Condições de Uso (AtividadeEpi)...");
        List<AtividadeEpi> relations = atividadeEpiRepository.findAll();
        Map<String, Long> conditionCounts = relations.stream()
                .map(rel -> rel.getCondicao() == null ? "NULL" : rel.getCondicao().trim())
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        System.out.println(" -> Distribuição de valores na coluna 'condicao':");
        conditionCounts.forEach((cond, count) -> System.out.println("    - '" + cond + "': " + count + " ocorrências"));
    }
}
