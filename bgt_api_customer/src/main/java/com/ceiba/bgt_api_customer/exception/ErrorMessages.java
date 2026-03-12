package com.ceiba.bgt_api_customer.exception;

/**
 * Centraliza todos los mensajes de error del dominio de clientes.
 */
public final class ErrorMessages {

    private ErrorMessages() {
        // Clase de constantes, no instanciable
    }

    // ── Cliente ───────────────────────────────────────────────────────────────
    public static final String CUSTOMER_NOT_FOUND      = "Cliente con el usuario: %s no existe";
    public static final String USERNAME_ALREADY_EXISTS = "El nombre de usuario '%s' ya se encuentra registrado";
    public static final String USERNAME_DUPLICATE      = "El nombre de usuario ya se encuentra registrado";

    // ── Validación de campos ──────────────────────────────────────────────────
    public static final String FIELD_REQUIRED          = "El campo '%s' es obligatorio";

    // ── Seguridad ─────────────────────────────────────────────────────────────
    public static final String INVALID_JWT_TOKEN = "Token JWT inválido o expirado";

    // ── Genérico ──────────────────────────────────────────────────────────────
    public static final String UNEXPECTED_ERROR = "Error inesperado en el servidor";
}
