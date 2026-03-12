package com.ceiba.bgt_api_notification.exception;

/**
 * Excepción lanzada cuando la entrada del cliente es inválida o incompleta.
 * Se mapea a HTTP 400 BAD_REQUEST.
 */
public class NotificationValidationException extends NotificationException {

    public NotificationValidationException(String message) {
        super(message);
    }
}
