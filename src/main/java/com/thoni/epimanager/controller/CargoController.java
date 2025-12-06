package com.thoni.epimanager.controller;

import com.thoni.epimanager.entity.Cargo;
import com.thoni.epimanager.repository.CargoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cargos")
@Tag(name = "Cargos", description = "Gestão de Cargos (funções) dentro de Atividades")
@SecurityRequirement(name = "basicAuth")
public class CargoController {

    private final CargoRepository cargoRepository;

    public CargoController(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos cargos", description = "Retorna lista completa de cargos cadastrados")
    public List<Cargo> listar() {
        return cargoRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cargo por ID", description = "Retorna um cargo específico pelo seu ID")
    public ResponseEntity<Cargo> buscarPorId(@PathVariable Long id) {
        return cargoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/atividade/{atividadeId}")
    @Operation(summary = "Listar cargos por atividade", description = "Retorna todos os cargos de uma atividade específica. Útil para popular dropdowns no app.")
    public List<Cargo> listarPorAtividade(
            @Parameter(description = "ID da atividade", example = "1") @PathVariable Long atividadeId) {
        return cargoRepository.findByAtividadeId(atividadeId);
    }
}
