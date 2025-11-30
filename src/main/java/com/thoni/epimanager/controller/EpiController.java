package com.thoni.epimanager.controller;

import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.service.EpiService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/epis")
@Tag(name = "EPIs", description = "Gerenciamento de Equipamentos de Proteção Individual")
@SecurityRequirement(name = "basicAuth")
public class EpiController {

    private final EpiService epiService;

    public EpiController(EpiService epiService) {
        this.epiService = epiService;
    }

    @GetMapping
    @Operation(summary = "Listar todos EPIs", description = "Retorna lista completa de EPIs cadastrados")
    public List<Epi> findAll() {
        return epiService.findAll();
    }

    @GetMapping("/recomendados")
    @Operation(summary = "EPIs recomendados por cargo", description = "Retorna EPIs obrigatórios e condicionais para um cargo específico")
    public List<Epi> findRecomendados(@RequestParam Long cargoId) {
        return epiService.findRecomendadosPorCargo(cargoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo EPI", description = "Cadastra um novo equipamento no sistema")
    public Epi create(@RequestBody Epi epi) {
        return epiService.save(epi);
    }
}
