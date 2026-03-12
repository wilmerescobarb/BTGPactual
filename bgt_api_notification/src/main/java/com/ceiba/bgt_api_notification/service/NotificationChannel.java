package com.ceiba.bgt_api_notification.service;

import com.ceiba.bgt_api_notification.dto.NotificationResponse;
import reactor.core.publisher.Mono;

/**
 * Contrato genérico para cualquier canal de notificación.
 *
 * <p>Cada implementación trabaja con su propio tipo de request ({@code T}),
 * lo que garantiza type-safety y facilita el testing mediante mocks de la interfaz.</p>
 *
 * <p>Nuevos canales (WhatsApp, Push, etc.) sólo requieren implementar esta interfaz
 * sin tocar el código existente (principio Open/Closed).</p>
 *
 * @param <T> tipo del request de notificación
 */
public interface NotificationChannel<T> {

    Mono<NotificationResponse> send(T request);
}
