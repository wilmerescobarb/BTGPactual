package com.ceiba.bgt_api_notification.dto;

/**
 * DTO tipado para las respuestas de error de la API.
 *
 * <p>Reemplaza el uso de {@code Map<String, Object>} en {@code GlobalExceptionHandler},
 * proporcionando un contrato explícito para los clientes de la API y
 * habilitando la documentación automática con OpenAPI/Swagger.</p>
 *
 * @param timestamp momento en que ocurrió el error (ISO-8601)
 * @param status    código HTTP numérico
 * @param error     descripción estándar del código HTTP
 * @param message   detalle del error para el cliente
 */
public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message
) {}
