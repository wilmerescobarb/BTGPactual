package com.ceiba.bgt_api_notification.util;

/**
 * Constantes compartidas del módulo de notificaciones.
 */
public final class NotificationConstants {

    private NotificationConstants() {}

    // ── Estado de la notificación ────────────────────────────────────────────
    public static final String STATUS_SENT = "SENT";

    // ── Enmascaramiento de datos sensibles ───────────────────────────────────
    /** Caracteres visibles del usuario en un email enmascarado. */
    public static final int EMAIL_VISIBLE_CHARS = 2;

    /** Caracteres visibles al inicio de un número de teléfono enmascarado. */
    public static final int PHONE_PREFIX_VISIBLE = 5;

    /** Caracteres visibles al final de un número de teléfono enmascarado. */
    public static final int PHONE_SUFFIX_VISIBLE = 4;

    /** Longitud mínima de un número de teléfono E.164 para poder enmascararlo. */
    public static final int PHONE_MIN_LENGTH = 9;

    /** Máscara de reemplazo para datos ocultos en logs. */
    public static final String MASK = "***";

    // ── Validación ───────────────────────────────────────────────────────────
    /** Expresión regular para números de teléfono en formato E.164. */
    public static final String E164_PATTERN = "^\\+[1-9]\\d{7,14}$";
}
