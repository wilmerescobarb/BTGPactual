package com.ceiba.bgt_api_notification.exception;

/**
 * Excepción lanzada cuando falla la entrega de la notificación
 * por un error del proveedor externo (SMTP, AWS SNS, etc.).
 * Se mapea a HTTP 502 BAD_GATEWAY.
 */
public class NotificationDeliveryException extends NotificationException {

    public NotificationDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
