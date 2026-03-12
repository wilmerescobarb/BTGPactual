package com.ceiba.bgt_api_investment.exception;

import com.ceiba.bgt_api_investment.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * Manejador global de excepciones para el API de inversiones.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura BusinessException y retorna HTTP 400 con el mensaje de la regla de negocio.
     */
    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleBusinessException(BusinessException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ex.getMessage(), null)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), null)));
    }

    /**
     * Captura tokens JWT inválidos o expirados y retorna HTTP 401.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleUnauthorizedException(UnauthorizedException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ex.getMessage(), null)));
    }

    /**
     * Captura IllegalStateException (ej: fondo referenciado no encontrado en DB).
     */
    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleIllegalStateException(IllegalStateException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(ErrorMessages.DATA_CONSISTENCY_ERROR, null)));
    }

    /**
     * Handler genérico para cualquier excepción no mapeada.
     * Registra el error internamente pero expone un mensaje seguro al cliente.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGenericException(Exception ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(ErrorMessages.UNEXPECTED_ERROR, null)));
    }
}
