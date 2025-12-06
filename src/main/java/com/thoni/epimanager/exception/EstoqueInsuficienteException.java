package com.thoni.epimanager.exception;

/**
 * Exception lançada quando o estoque de um EPI é insuficiente
 */
public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
}
