package com.ceiba.bgt_api_investment.exception;

/**
 * Centraliza todos los mensajes de error del dominio de inversiones.
 * Facilita la localización, consistencia y mantenimiento de los mensajes.
 */
public final class ErrorMessages {

    private ErrorMessages() {
        // Clase de constantes, no instanciable
    }

    // ── Cliente ───────────────────────────────────────────────────────────────
    public static final String CUSTOMER_NOT_FOUND = "Cliente con el usuario: %s no existe";

    // ── Fondo de inversión ────────────────────────────────────────────────────
    public static final String INVESTMENT_NOT_FOUND = "No existe fondo de inversión con el id: %s";
    public static final String INVESTMENT_DATA_INCONSISTENT = "Fondo no encontrado: %s";

    // ── Suscripción ───────────────────────────────────────────────────────────
    public static final String SUBSCRIPTION_NOT_FOUND = "No existe suscripción con el id: %s";
    public static final String SUBSCRIPTION_NOT_OWNER = "La suscripción no pertenece al cliente autenticado";
    public static final String SUBSCRIPTION_ALREADY_CANCELLED = "La suscripción ya se encuentra cancelada";

    // ── Reglas de negocio ─────────────────────────────────────────────────────
    public static final String AMOUNT_BELOW_MINIMUM = "El monto es inferior al mínimo permitido para la inversión";
    public static final String INSUFFICIENT_BALANCE = "No tiene saldo disponible para vincularse al fondo %s";

    // ── Seguridad ─────────────────────────────────────────────────────────────
    public static final String INVALID_JWT_TOKEN = "Token JWT inválido o expirado";

    // ── Errores del servidor ───────────────────────────────────────────────
    public static final String DATA_CONSISTENCY_ERROR = "Error de consistencia de datos";
    public static final String UNEXPECTED_ERROR = "Ha ocurrido un error inesperado";
}
