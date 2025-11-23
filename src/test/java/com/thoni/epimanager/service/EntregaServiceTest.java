package com.thoni.epimanager.service;

import com.thoni.epimanager.entity.Entrega;
import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.entity.Funcionario;
import com.thoni.epimanager.repository.EntregaRepository;
import com.thoni.epimanager.repository.EpiRepository;
import com.thoni.epimanager.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Entregas")
class EntregaServiceTest {

    @Mock
    private EntregaRepository entregaRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private EpiRepository epiRepository;

    @InjectMocks
    private EntregaService entregaService;

    private Funcionario funcionario;
    private Epi epi;

    @BeforeEach
    void setUp() {
        funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNome("João");

        epi = new Epi();
        epi.setId(1L);
        epi.setNome("Luva");
        epi.setEstoqueAtual(10);
    }

    @Test
    @DisplayName("Deve registrar entrega com sucesso e calcular data limite de troca")
    void registrarEntrega_ShouldCreateDeliveryAndCalculateLimitDate() {
        epi.setLimiteTrocaEmDias(180);
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(epiRepository.findById(1L)).thenReturn(Optional.of(epi));
        when(entregaRepository.save(any(Entrega.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Entrega result = entregaService.registrarEntrega(1L, 1L, "foto.jpg", "ass.png");

        assertNotNull(result);
        assertEquals(9, epi.getEstoqueAtual());
        assertNotNull(result.getDataLimiteTroca());
        assertEquals(LocalDate.now().plusDays(180), result.getDataLimiteTroca());
        verify(entregaRepository, times(1)).save(any(Entrega.class));
    }

    @Test
    @DisplayName("Deve listar entregas próximas do vencimento")
    void listarVencimentosProximos_ShouldReturnDeliveries() {
        LocalDate dataLimite = LocalDate.now().plusDays(7);
        when(entregaRepository.findVencendoAte(dataLimite)).thenReturn(List.of(new Entrega()));

        List<Entrega> result = entregaService.listarVencimentosProximos(7);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(entregaRepository, times(1)).findVencendoAte(dataLimite);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar entregar EPI sem estoque")
    void registrarEntrega_ShouldThrowException_WhenEpiOutOfStock() {
        epi.setEstoqueAtual(0);
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(epiRepository.findById(1L)).thenReturn(Optional.of(epi));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entregaService.registrarEntrega(1L, 1L, "foto.jpg", "ass.png");
        });

        assertEquals("EPI sem estoque", exception.getMessage());
        verify(entregaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando funcionário não for encontrado")
    void registrarEntrega_ShouldThrowException_WhenFuncionarioNotFound() {
        when(funcionarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            entregaService.registrarEntrega(99L, 1L, "foto.jpg", "ass.png");
        });
    }
}
