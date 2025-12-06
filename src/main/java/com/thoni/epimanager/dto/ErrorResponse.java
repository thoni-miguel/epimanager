package com.thoni.epimanager.dto;

import java.time.LocalDateTime;

/**
 * DTO padrão para resposta de erros
 */
public record ErrorResponse(
        String code, // Código do erro (ex: "ESTOQUE_INSUFICIENTE")
        String message, // Mensagem descritiva
        LocalDateTime timestamp // Momento do erro
) {
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now());
    }
}
