package com.ceiba.bgt_api_investment.controller;

import com.ceiba.bgt_api_investment.dto.ApiResponse;
import com.ceiba.bgt_api_investment.dto.CustomerInvestmentDto;
import com.ceiba.bgt_api_investment.dto.InvestmentSummaryDto;
import com.ceiba.bgt_api_investment.service.CustomerInvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Controlador para el API de inversiones.
 *
 * El parámetro anotado con @AuthenticationPrincipal recibe el "principal"
 * establecido en JwtAuthenticationWebFilter (el username extraído del JWT).
 */
@RestController
@RequestMapping("/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final CustomerInvestmentService customerInvestmentService;

    @GetMapping("/me")
    public Mono<Map<String, String>> getCurrentCustomer(
            @AuthenticationPrincipal String username) {
        return Mono.just(Map.of("authenticatedAs", username));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<InvestmentSummaryDto>>>> getInvestments(
            @AuthenticationPrincipal String username) {
        return customerInvestmentService.getInvestments(username)
                .collectList()
                .map(list -> ResponseEntity.ok(
                        new ApiResponse<>("Inversiones del cliente", list)));
    }

    @PostMapping("/subscribe")
    public Mono<ResponseEntity<ApiResponse<CustomerInvestmentDto>>> subscribe(
            @AuthenticationPrincipal String username,
            @RequestBody CustomerInvestmentDto request) {
        return customerInvestmentService.subscribe(username, request)
                .map(dto -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new ApiResponse<>("Suscripción creada exitosamente", dto)));
    }

    @PutMapping("/unsubscribe/{id_customer_investment}")
    public Mono<ResponseEntity<ApiResponse<CustomerInvestmentDto>>> unsubscribe(
            @AuthenticationPrincipal String username,
            @PathVariable("id_customer_investment") Integer idCustomerInvestment) {
        return customerInvestmentService.unsubscribe(username, idCustomerInvestment)
                .map(dto -> ResponseEntity
                        .ok(new ApiResponse<>("Suscripción cancelada exitosamente", dto)));
    }
}
