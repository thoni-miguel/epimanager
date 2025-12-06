package com.thoni.epimanager.exception;

/**
 * Exception lançada quando há conflito de negócio (ex: devolução duplicada)
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
