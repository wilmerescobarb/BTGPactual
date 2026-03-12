package com.ceiba.bgt_api_notification.util;

/**
 * Mensajes de error de validación y de entrega usados en excepciones y anotaciones JSR-303.
 */
public final class ErrorMessages {

    private ErrorMessages() {}

    // ── Validación de EmailRequest ───────────────────────────────────────────
    public static final String EMAIL_TO_REQUIRED        = "El campo 'to' es requerido";
    public static final String EMAIL_TO_INVALID         = "El campo 'to' debe ser un correo electrónico válido";
    public static final String EMAIL_SUBJECT_REQUIRED   = "El campo 'subject' es requerido";
    public static final String EMAIL_MESSAGE_REQUIRED   = "El campo 'message' es requerido";

    // ── Validación de SmsRequest ─────────────────────────────────────────────
    public static final String SMS_PHONE_REQUIRED       = "El campo 'phoneNumber' es requerido";
    public static final String SMS_PHONE_INVALID        = "El campo 'phoneNumber' debe tener formato E.164 (ej: +573001234567)";
    public static final String SMS_MESSAGE_REQUIRED     = "El campo 'message' es requerido";

    // ── Errores de entrega ───────────────────────────────────────────────────
    public static final String EMAIL_DELIVERY_ERROR     = "Error al enviar email: ";
    public static final String SMS_DELIVERY_ERROR       = "Error al enviar SMS: ";

    // ── Error genérico ───────────────────────────────────────────────────────
    public static final String INTERNAL_SERVER_ERROR    = "Error interno del servidor";

    // ── Seguridad / JWT ──────────────────────────────────────────────────────
    public static final String INVALID_JWT_TOKEN        = "Token JWT inválido o expirado";
    public static final String UNAUTHORIZED             = "No autorizado: se requiere un token válido";
}
