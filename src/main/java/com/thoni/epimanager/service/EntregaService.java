package com.thoni.epimanager.service;

import com.thoni.epimanager.entity.Entrega;
import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.entity.Funcionario;
import com.thoni.epimanager.repository.EntregaRepository;
import com.thoni.epimanager.repository.EpiRepository;
import com.thoni.epimanager.repository.FuncionarioRepository;
import com.thoni.epimanager.exception.ResourceNotFoundException;
import com.thoni.epimanager.exception.EstoqueInsuficienteException;
import com.thoni.epimanager.exception.BusinessException;
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
    private final StorageService storageService;

    public EntregaService(EntregaRepository entregaRepository, FuncionarioRepository funcionarioRepository,
            EpiRepository epiRepository, StorageService storageService) {
        this.entregaRepository = entregaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.epiRepository = epiRepository;
        this.storageService = storageService;
    }

    @Transactional
    public Entrega registrarEntrega(Long funcionarioId, Long epiId, String fotoBase64, String assinaturaBase64) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Funcionário não encontrado com ID: " + funcionarioId));

        Epi epi = epiRepository.findById(epiId)
                .orElseThrow(() -> new ResourceNotFoundException("EPI não encontrado com ID: " + epiId));

        if (epi.getEstoqueAtual() <= 0) {
            throw new EstoqueInsuficienteException("EPI '" + epi.getNome() + "' sem estoque disponível");
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

        // Processar e salvar foto (Base64 -> arquivo)
        if (fotoBase64 != null && !fotoBase64.isEmpty()) {
            String fotoPath = storageService.saveBase64Image(fotoBase64, "foto");
            entrega.setFotoPath(fotoPath);
        }

        // Processar e salvar assinatura (Base64 -> arquivo)
        if (assinaturaBase64 != null && !assinaturaBase64.isEmpty()) {
            String assinaturaPath = storageService.saveBase64Image(assinaturaBase64, "assinatura");
            entrega.setAssinaturaPath(assinaturaPath);
        }

        return entregaRepository.save(entrega);
    }

    public List<Entrega> listarVencimentosProximos(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return entregaRepository.findVencendoAte(dataLimite);
    }

    /**
     * Registra a devolução de um EPI
     * - Marca data de devolução
     * - Incrementa estoque de volta
     * 
     * @param entregaId ID da entrega a ser devolvida
     * @return Entrega atualizada
     */
    @Transactional
    public Entrega registrarDevolucao(Long entregaId) {
        // Buscar entrega
        Entrega entrega = entregaRepository.findById(entregaId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega não encontrada com ID: " + entregaId));

        // Validar se já não foi devolvida
        if (entrega.getDataDevolucao() != null) {
            throw new BusinessException("EPI já foi devolvido anteriormente em " + entrega.getDataDevolucao());
        }

        // Incrementar estoque de volta
        Epi epi = entrega.getEpi();
        epi.setEstoqueAtual(epi.getEstoqueAtual() + 1);
        epiRepository.save(epi);

        // Marcar como devolvido
        entrega.setDataDevolucao(LocalDate.now());

        return entregaRepository.save(entrega);
    }
}
