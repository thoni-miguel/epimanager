package com.thoni.epimanager.config;

import com.thoni.epimanager.entity.Atividade;
import com.thoni.epimanager.entity.AtividadeEpi;
import com.thoni.epimanager.entity.Cargo;
import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.repository.AtividadeEpiRepository;
import com.thoni.epimanager.repository.AtividadeRepository;
import com.thoni.epimanager.repository.CargoRepository;
import com.thoni.epimanager.repository.EpiRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private final AtividadeRepository atividadeRepository;
    private final CargoRepository cargoRepository;
    private final EpiRepository epiRepository;
    private final AtividadeEpiRepository atividadeEpiRepository;

    public DatabaseSeeder(AtividadeRepository atividadeRepository, CargoRepository cargoRepository,
            EpiRepository epiRepository, AtividadeEpiRepository atividadeEpiRepository) {
        this.atividadeRepository = atividadeRepository;
        this.cargoRepository = cargoRepository;
        this.epiRepository = epiRepository;
        this.atividadeEpiRepository = atividadeEpiRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Clear DB for MVP
        atividadeEpiRepository.deleteAll();
        cargoRepository.deleteAll();
        atividadeRepository.deleteAll();
        epiRepository.deleteAll();

        System.out.println("Database cleared. Seeding from Markdown...");

        Path mdPath = Paths.get("docs/especific_epi_infos.md");

        if (!Files.exists(mdPath)) {
            System.err.println("Markdown file not found at: " + mdPath.toAbsolutePath());
            return;
        }

        List<String> lines = Files.readAllLines(mdPath);
        parseAndSeed(lines);

        System.out.println("Seeding complete!");
    }

    private void parseAndSeed(List<String> lines) {
        Atividade currentAtividade = null;
        String currentSection = "NONE"; // NONE, CARGOS, EPIS, CONDICIONAIS, EXAMES

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            // Detect Activity Block (Level 1: * **ACTIVITY**)
            if (line.startsWith("* **") && !line.contains("Cargos:") && !line.contains("EPIs")
                    && !line.contains("Exames:")) {
                String atividadeName = extractItemContent(line);
                currentAtividade = atividadeRepository.findByNome(atividadeName)
                        .orElseGet(() -> {
                            Atividade novaAtividade = new Atividade();
                            novaAtividade.setNome(atividadeName);
                            return atividadeRepository.save(novaAtividade);
                        });
                currentSection = "NONE";
                continue;
            }

            if (currentAtividade == null)
                continue;

            // Detect Sections
            if (line.startsWith("* **Cargos:**")) {
                currentSection = "CARGOS";
            } else if (line.startsWith("* **EPIs (Equipamentos")) {
                currentSection = "EPIS";
            } else if (line.startsWith("* **EPIs Condicionais:**")) {
                currentSection = "CONDICIONAIS";
            } else if (line.startsWith("* **Exames:**")) {
                currentSection = "EXAMES";
            } else if (line.startsWith("*") || line.startsWith("-")) {
                // Item
                String content = extractItemContent(line);
                if (content == null || content.isBlank() || content.equalsIgnoreCase("Sem condicionais"))
                    continue;

                switch (currentSection) {
                    case "CARGOS":
                        Cargo cargo = new Cargo();
                        cargo.setNome(content);
                        cargo.setAtividade(currentAtividade);
                        cargoRepository.save(cargo);
                        break;
                    case "EPIS":
                        saveEpiRelation(currentAtividade, content, null);
                        break;
                    case "CONDICIONAIS":
                        String condicao = extractCondition(content);
                        String epiName = cleanEpiName(content);
                        saveEpiRelation(currentAtividade, epiName, condicao);
                        break;
                }
            }
        }
    }

    private void saveEpiRelation(Atividade atividade, String epiName, String condicao) {
        Epi epi = epiRepository.findByNome(epiName).orElseGet(() -> {
            Epi newEpi = new Epi();
            newEpi.setNome(epiName);
            newEpi.setCa("00000");
            newEpi.setEstoqueAtual(100);
            newEpi.setCustoUnitario(BigDecimal.ZERO);
            return epiRepository.save(newEpi);
        });

        AtividadeEpi relacao = new AtividadeEpi();
        relacao.setAtividade(atividade);
        relacao.setEpi(epi);
        relacao.setCondicao(condicao);
        atividadeEpiRepository.save(relacao);
    }

    private String extractItemContent(String line) {
        // Remove leading *, -, spaces, and bold markers
        return line.replaceAll("^\\s*[*-]\\s*", "").replaceAll("\\*\\*", "").trim();
    }

    private String cleanEpiName(String name) {
        // Remove prefix "Ruído: "
        return name.replaceAll("^.*:\\s+", "").trim();
    }

    private String extractCondition(String name) {
        if (name.contains(":")) {
            return name.substring(0, name.indexOf(":")).trim();
        }
        return "Condicional";
    }
}
