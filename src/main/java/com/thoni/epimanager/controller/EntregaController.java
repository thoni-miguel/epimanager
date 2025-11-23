package com.thoni.epimanager.controller;

import com.thoni.epimanager.dto.EntregaRequest;
import com.thoni.epimanager.entity.Entrega;
import com.thoni.epimanager.service.EntregaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entregas")
public class EntregaController {

    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @PostMapping
    public ResponseEntity<Entrega> registrarEntrega(@RequestBody EntregaRequest request) {
        Entrega entrega = entregaService.registrarEntrega(
                request.funcionarioId(),
                request.epiId(),
                request.fotoPath(),
                request.assinaturaPath());
        return ResponseEntity.ok(entrega);
    }

    @GetMapping("/vencendo")
    public ResponseEntity<List<Entrega>> listarVencimentos(@RequestParam(defaultValue = "7") int dias) {
        List<Entrega> entregas = entregaService.listarVencimentosProximos(dias);
        return ResponseEntity.ok(entregas);
    }
}
