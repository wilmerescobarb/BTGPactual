package com.ceiba.bgt_api_notification.exception;

/**
 * Excepción lanzada cuando un token JWT es inválido, está expirado o ausente.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
