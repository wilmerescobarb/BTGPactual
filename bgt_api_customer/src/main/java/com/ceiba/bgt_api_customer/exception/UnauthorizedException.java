package com.ceiba.bgt_api_customer.exception;

/**
 * Excepción lanzada cuando el token JWT es inválido, expirado o ausente.
 * Corresponde a HTTP 401 UNAUTHORIZED.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
