package com.ceiba.bgt_api_customer.exception;

import com.ceiba.bgt_api_customer.dto.ApiResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * Manejador global de excepciones para el API de clientes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleUnauthorizedException(UnauthorizedException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ex.getMessage(), null)));
    }

    /**
     * Captura violaciones de índice único (username duplicado).
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDuplicateKeyException(DuplicateKeyException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(ErrorMessages.USERNAME_DUPLICATE, null)));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGenericException(Exception ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(ErrorMessages.UNEXPECTED_ERROR, null)));
    }
}
