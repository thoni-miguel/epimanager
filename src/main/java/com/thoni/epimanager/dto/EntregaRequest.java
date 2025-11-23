package com.thoni.epimanager.dto;

import jakarta.validation.constraints.NotNull;

public record EntregaRequest(
                @NotNull Long funcionarioId,
                @NotNull Long epiId,
                String fotoPath,
                String assinaturaPath) {
}
