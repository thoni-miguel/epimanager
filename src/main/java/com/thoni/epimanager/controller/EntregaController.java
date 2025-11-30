package com.thoni.epimanager.controller;

import com.thoni.epimanager.dto.EntregaRequest;
import com.thoni.epimanager.entity.Entrega;
import com.thoni.epimanager.service.EntregaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/entregas")
@Tag(name = "Entregas", description = "Registro e consulta de entregas de EPIs")
@SecurityRequirement(name = "basicAuth")
public class EntregaController {

    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @PostMapping
    @Operation(summary = "Registrar entrega", description = "Registra entrega de EPI a funcionário. Decrementa estoque automaticamente.")
    public ResponseEntity<Entrega> registrarEntrega(@RequestBody EntregaRequest request) {
        Entrega entrega = entregaService.registrarEntrega(
                request.funcionarioId(),
                request.epiId(),
                request.fotoPath(),
                request.assinaturaPath());
        return ResponseEntity.ok(entrega);
    }

    @GetMapping("/vencendo")
    @Operation(summary = "Listar entregas vencendo", description = "Retorna EPIs próximos do vencimento (data limite de troca)")
    public ResponseEntity<List<Entrega>> listarVencimentos(
            @Parameter(description = "Número de dias à frente", example = "7") @RequestParam(defaultValue = "7") int dias) {
        List<Entrega> entregas = entregaService.listarVencimentosProximos(dias);
        return ResponseEntity.ok(entregas);
    }
}
