package com.ceiba.bgt_api_investment.exception;

/**
 * Excepción que representa una violación a las reglas de negocio.
 * Se mapea automáticamente a HTTP 400 por el GlobalExceptionHandler.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
