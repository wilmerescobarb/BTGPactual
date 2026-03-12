package com.ceiba.bgt_api_notification.exception;

import com.ceiba.bgt_api_notification.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static com.ceiba.bgt_api_notification.util.ErrorMessages.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_BIND_ERROR        = "Error de validación en request: {}";
    private static final String LOG_VALIDATION_FAILED = "Validación fallida: {}";
    private static final String LOG_DELIVERY_ERROR    = "Error de entrega con proveedor externo: {}";
    private static final String LOG_UNAUTHORIZED      = "Acceso no autorizado: {}";
    private static final String LOG_UNEXPECTED_ERROR  = "Error inesperado: {}";

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBindException(WebExchangeBindException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn(LOG_BIND_ERROR, errors);
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, errors)));
    }

    @ExceptionHandler(NotificationValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(NotificationValidationException ex) {
        log.warn(LOG_VALIDATION_FAILED, ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage())));
    }

    @ExceptionHandler(NotificationDeliveryException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDeliveryException(NotificationDeliveryException ex) {
        log.error(LOG_DELIVERY_ERROR, ex.getMessage(), ex);
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(buildError(HttpStatus.BAD_GATEWAY, ex.getMessage())));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn(LOG_UNAUTHORIZED, ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
        log.error(LOG_UNEXPECTED_ERROR, ex.getMessage(), ex);
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR)));
    }

    private ErrorResponse buildError(HttpStatus status, String message) {
        return new ErrorResponse(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
    }
}

