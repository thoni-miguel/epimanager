package com.thoni.epimanager.dto;

import jakarta.validation.constraints.NotNull;

public record EntregaRequest(
        @NotNull Long funcionarioId,
        @NotNull Long epiId,
        String fotoBase64, // Base64 da foto (opcional)
        String assinaturaBase64 // Base64 da assinatura (opcional)
) {
}
