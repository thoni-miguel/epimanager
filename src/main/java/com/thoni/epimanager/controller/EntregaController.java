package com.thoni.epimanager.controller;

import com.thoni.epimanager.dto.EntregaRequest;
import com.thoni.epimanager.entity.Entrega;
import com.thoni.epimanager.service.EntregaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entregas")
public class EntregaController {

    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Entrega create(@RequestBody @Valid EntregaRequest request) {
        return entregaService.registrarEntrega(
                request.funcionarioId(),
                request.epiId(),
                request.fotoPath(),
                request.assinaturaPath());
    }
}
