package com.ceiba.bgt_api_investment.service;

import com.ceiba.bgt_api_investment.dto.CustomerInvestmentDto;
import com.ceiba.bgt_api_investment.dto.InvestmentSummaryDto;
import com.ceiba.bgt_api_investment.exception.BusinessException;
import com.ceiba.bgt_api_investment.model.CustomerInvestment;
import com.ceiba.bgt_api_investment.repository.CustomerInvestmentRepository;
import com.ceiba.bgt_api_investment.repository.CustomerRepository;
import com.ceiba.bgt_api_investment.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Servicio que gestiona la suscripción de un cliente a un fondo de inversión.
 */
@Service
@RequiredArgsConstructor
public class CustomerInvestmentService {

    private final CustomerRepository customerRepository;
    private final InvestmentRepository investmentRepository;
    private final CustomerInvestmentRepository customerInvestmentRepository;

    public Flux<InvestmentSummaryDto> getInvestments(String username) {
        return customerInvestmentRepository.findInvestmentsByUsername(username);
    }

    public Mono<CustomerInvestmentDto> unsubscribe(String username, Integer idCustomerInvestment) {

        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Cliente con el usuario: " + username + " no existe")))
                .flatMap(customer ->
                        customerInvestmentRepository.findById(idCustomerInvestment)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                        "No existe suscripción con el id: " + idCustomerInvestment)))
                                .flatMap(ci -> {

                                    // Regla 1: la inversión debe pertenecer al customer autenticado
                                    if (!ci.getIdCustomer().equals(customer.getId())) {
                                        return Mono.error(new BusinessException(
                                                "La suscripción no pertenece al cliente autenticado"));
                                    }

                                    // Regla 2: no debe estar ya cancelada
                                    if ("C".equals(ci.getStatus())) {
                                        return Mono.error(new BusinessException(
                                                "La suscripción ya se encuentra cancelada"));
                                    }

                                    // Devolver el invested_amount al saldo del customer
                                    BigDecimal newBalance = customer.getAmount().add(ci.getInvestedAmount());
                                    LocalDateTime closedAt = LocalDateTime.now();

                                    return customerRepository.updateAmount(customer.getId(), newBalance)
                                            .flatMap(updated ->
                                                    customerInvestmentRepository.updateStatus(ci.getId(), "C", closedAt)
                                                            .map(rows -> CustomerInvestmentDto.from(
                                                                    CustomerInvestment.builder()
                                                                            .id(ci.getId())
                                                                            .idCustomer(ci.getIdCustomer())
                                                                            .idInvestment(ci.getIdInvestment())
                                                                            .openedAt(ci.getOpenedAt())
                                                                            .closedAt(closedAt)
                                                                            .investedAmount(ci.getInvestedAmount())
                                                                            .status("C")
                                                                            .build())));
                                })
                );
    }

    public Mono<CustomerInvestmentDto> subscribe(String username, CustomerInvestmentDto request) {

        LocalDateTime openedAt = LocalDateTime.now();

        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Cliente con el usuario: " + username+" no existe")))
                .flatMap(customer ->
                        investmentRepository.findById(request.getInvestment())
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                        "No existe fondo de inversión con el id: " + request.getInvestment())))
                                .flatMap(investment -> {

                                    // Regla 1: amount >= min_amount del fondo
                                    if (request.getAmount().compareTo(investment.getMinAmount()) < 0) {
                                        return Mono.error(new BusinessException(
                                                "El monto es inferior al mínimo permitido para la inversión"));
                                    }

                                    // Regla 2: el customer debe tener saldo suficiente
                                    BigDecimal newBalance = customer.getAmount().subtract(request.getAmount());
                                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                                        return Mono.error(new BusinessException(
                                                "No tiene saldo disponible para vincularse al fondo "
                                                        + investment.getName()));
                                    }

                                    // Descontar el monto del saldo del customer
                                    return customerRepository.updateAmount(customer.getId(), newBalance)
                                            .flatMap(updated -> {
                                                CustomerInvestment entity = CustomerInvestment.builder()
                                                        .idCustomer(customer.getId())
                                                        .idInvestment(investment.getId())
                                                        .openedAt(openedAt)
                                                        .investedAmount(request.getAmount())
                                                        .status("A")
                                                        .build();

                                                return customerInvestmentRepository.save(entity)
                                                        .map(CustomerInvestmentDto::from);
                                            });
                                })
                );
    }
}
