package com.ceiba.bgt_api_customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Envuelve cualquier respuesta de la API con un campo "data" y un "message".
 *
 * @param <T> tipo del payload de la respuesta
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private String message;
    private T data;
}
