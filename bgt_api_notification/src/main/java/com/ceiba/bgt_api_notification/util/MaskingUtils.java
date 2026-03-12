package com.ceiba.bgt_api_notification.util;

/**
 * Utilidades para el enmascaramiento de datos sensibles (PII) en logs.
 *
 * <p>Centraliza la lógica de ofuscación para que cualquier canal de notificación
 * actual o futuro pueda reutilizarla sin duplicar código.</p>
 */
public final class MaskingUtils {

    private MaskingUtils() {}

    /**
     * Enmascara un email para logs: muestra los 2 primeros caracteres del usuario,
     * reemplaza el resto con {@link NotificationConstants#MASK} y conserva el dominio completo.
     * <p>Ejemplo: {@code wilmer.escobar@ceiba.com.co} → {@code wi***@ceiba.com.co}</p>
     *
     * @param email dirección de correo a enmascarar
     * @return email enmascarado, o {@link NotificationConstants#MASK} si el valor es inválido
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return NotificationConstants.MASK;
        int atIndex = email.indexOf('@');
        String user = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        String visible = user.length() > NotificationConstants.EMAIL_VISIBLE_CHARS
                ? user.substring(0, NotificationConstants.EMAIL_VISIBLE_CHARS)
                : user;
        return visible + NotificationConstants.MASK + domain;
    }

    /**
     * Enmascara un número de teléfono E.164 para logs: muestra los 5 primeros
     * caracteres y los 4 últimos, ocultando el centro con {@link NotificationConstants#MASK}.
     * <p>Ejemplo: {@code +573001234567} → {@code +5730***4567}</p>
     *
     * @param phone número de teléfono a enmascarar
     * @return teléfono enmascarado, o {@link NotificationConstants#MASK} si el valor es inválido
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < NotificationConstants.PHONE_MIN_LENGTH)
            return NotificationConstants.MASK;
        return phone.substring(0, NotificationConstants.PHONE_PREFIX_VISIBLE)
                + NotificationConstants.MASK
                + phone.substring(phone.length() - NotificationConstants.PHONE_SUFFIX_VISIBLE);
    }
}
