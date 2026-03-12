package com.ceiba.bgt_api_customer.controller;

import com.ceiba.bgt_api_customer.constant.ResponseMessages;
import com.ceiba.bgt_api_customer.dto.ApiResponse;
import com.ceiba.bgt_api_customer.dto.CustomerDto;
import com.ceiba.bgt_api_customer.dto.RegisterRequest;
import com.ceiba.bgt_api_customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controlador para el API de clientes.
 *
 * GET  /customer  → retorna la información del cliente autenticado (requiere JWT)
 * POST /customer  → registra un nuevo cliente (público)
 */
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<CustomerDto>>> getCustomer(
            @AuthenticationPrincipal String username) {
        return customerService.getCustomer(username)
                .map(dto -> ResponseEntity.ok(
                        new ApiResponse<>(ResponseMessages.GET_CUSTOMER_OK, dto)));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<CustomerDto>>> register(
            @RequestBody RegisterRequest request) {
        return customerService.register(request)
                .map(dto -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(ResponseMessages.REGISTER_CUSTOMER_OK, dto)));
    }
}
