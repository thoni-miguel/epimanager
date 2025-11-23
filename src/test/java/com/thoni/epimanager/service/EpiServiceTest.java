package com.thoni.epimanager.service;

import com.thoni.epimanager.entity.Atividade;
import com.thoni.epimanager.entity.AtividadeEpi;
import com.thoni.epimanager.entity.Cargo;
import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.repository.AtividadeEpiRepository;
import com.thoni.epimanager.repository.CargoRepository;
import com.thoni.epimanager.repository.EpiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de EPIs")
class EpiServiceTest {

    @Mock
    private EpiRepository epiRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private AtividadeEpiRepository atividadeEpiRepository;

    @InjectMocks
    private EpiService epiService;

    private Cargo cargo;
    private Atividade atividade;
    private Epi epi1;
    private Epi epi2;

    @BeforeEach
    void setUp() {
        atividade = new Atividade();
        atividade.setId(1L);
        atividade.setNome("Atividade Teste");

        cargo = new Cargo();
        cargo.setId(1L);
        cargo.setNome("Cargo Teste");
        cargo.setAtividade(atividade);

        epi1 = new Epi();
        epi1.setId(1L);
        epi1.setNome("EPI 1");

        epi2 = new Epi();
        epi2.setId(2L);
        epi2.setNome("EPI 2");
    }

    @Test
    @DisplayName("Deve retornar todos os EPIs cadastrados")
    void findAll_ShouldReturnAllEpis() {
        when(epiRepository.findAll()).thenReturn(Arrays.asList(epi1, epi2));

        List<Epi> result = epiService.findAll();

        assertEquals(2, result.size());
        verify(epiRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar EPIs recomendados para o cargo (via atividade)")
    void findRecomendadosPorCargo_ShouldReturnEpisForActivity() {
        AtividadeEpi ae1 = new AtividadeEpi();
        ae1.setAtividade(atividade);
        ae1.setEpi(epi1);

        AtividadeEpi ae2 = new AtividadeEpi();
        ae2.setAtividade(atividade);
        ae2.setEpi(epi2);

        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(atividadeEpiRepository.findByAtividadeId(1L)).thenReturn(Arrays.asList(ae1, ae2));

        List<Epi> result = epiService.findRecomendadosPorCargo(1L);

        assertEquals(2, result.size());
        assertTrue(result.contains(epi1));
        assertTrue(result.contains(epi2));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cargo não for encontrado")
    void findRecomendadosPorCargo_ShouldThrowException_WhenCargoNotFound() {
        when(cargoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> epiService.findRecomendadosPorCargo(99L));
    }
}
