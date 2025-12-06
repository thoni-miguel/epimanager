package com.thoni.epimanager.controller;

import com.thoni.epimanager.entity.Atividade;
import com.thoni.epimanager.repository.AtividadeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atividades")
@Tag(name = "Atividades", description = "Gestão de Atividades (contextos de trabalho)")
@SecurityRequirement(name = "basicAuth")
public class AtividadeController {

    private final AtividadeRepository atividadeRepository;

    public AtividadeController(AtividadeRepository atividadeRepository) {
        this.atividadeRepository = atividadeRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todas atividades", description = "Retorna lista completa de atividades cadastradas")
    public List<Atividade> listar() {
        return atividadeRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar atividade por ID", description = "Retorna uma atividade específica pelo seu ID")
    public ResponseEntity<Atividade> buscarPorId(@PathVariable Long id) {
        return atividadeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
