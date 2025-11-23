package com.thoni.epimanager.service;

import com.thoni.epimanager.entity.Entrega;
import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.entity.Funcionario;
import com.thoni.epimanager.repository.EntregaRepository;
import com.thoni.epimanager.repository.EpiRepository;
import com.thoni.epimanager.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EpiRepository epiRepository;

    public EntregaService(EntregaRepository entregaRepository, FuncionarioRepository funcionarioRepository,
            EpiRepository epiRepository) {
        this.entregaRepository = entregaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.epiRepository = epiRepository;
    }

    @Transactional
    public Entrega registrarEntrega(Long funcionarioId, Long epiId, String fotoPath, String assinaturaPath) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        Epi epi = epiRepository.findById(epiId)
                .orElseThrow(() -> new RuntimeException("EPI não encontrado"));

        if (epi.getEstoqueAtual() <= 0) {
            throw new RuntimeException("EPI sem estoque");
        }

        // Decrement stock
        epi.setEstoqueAtual(epi.getEstoqueAtual() - 1);
        epiRepository.save(epi);

        // Create delivery record
        Entrega entrega = new Entrega();
        entrega.setFuncionario(funcionario);
        entrega.setEpi(epi);
        entrega.setDataEntrega(LocalDate.now());

        // Calculate limit date based on EPI durability
        if (epi.getLimiteTrocaEmDias() != null) {
            entrega.setDataLimiteTroca(LocalDate.now().plusDays(epi.getLimiteTrocaEmDias()));
        }

        entrega.setFotoPath(fotoPath);
        entrega.setAssinaturaPath(assinaturaPath);

        return entregaRepository.save(entrega);
    }

    public List<Entrega> listarVencimentosProximos(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return entregaRepository.findVencendoAte(dataLimite);
    }
}
